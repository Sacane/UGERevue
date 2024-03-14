package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewOnReviewDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewRemoveDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.DetailReviewResponseDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewQuestionResponseDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewResponseChildrenDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewResponseDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.Tag;
import fr.pentagon.ugeoverflow.model.vote.ReviewVote;
import fr.pentagon.ugeoverflow.model.vote.ReviewVoteId;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.ReviewVoteRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final QuestionRepository questionRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewVoteRepository reviewVoteRepository;

    private final Logger logger = Logger.getLogger(ReviewService.class.getName());

    public ReviewService(QuestionRepository questionRepository, ReviewRepository reviewRepository, UserRepository userRepository, ReviewVoteRepository reviewVoteRepository) {
        this.questionRepository = questionRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.reviewVoteRepository = reviewVoteRepository;
    }

    List<ReviewResponseDTO> getReviews(long reviewId) {
        var review = reviewRepository.findByIdWithReviews(reviewId)
                .orElseThrow(() -> HttpException.notFound("Review not exist"));
        var author = review.getAuthor();

        return review.getReviews()
                .stream()
                .map(r -> new ReviewResponseDTO(
                        author.getUsername(),
                        r.getContent(),
                        reviewVoteRepository.findUpvoteNumberByReviewId(r.getId()),
                        reviewVoteRepository.findDownvoteNumberByReviewId(r.getId()),
                        r.getCreatedAt())
                ).toList();
    }

    @Transactional
    public ReviewQuestionResponseDTO addReview(ReviewOnReviewDTO reviewOnReviewDTO) {
        var user = userRepository.findById(reviewOnReviewDTO.userId())
                .orElseThrow(() -> HttpException.notFound("User not exist"));
        var review = reviewRepository.findById(reviewOnReviewDTO.reviewId())
                .orElseThrow(() -> HttpException.notFound("Review not exist"));

        var newReview = reviewRepository.save(new Review(reviewOnReviewDTO.content(), null, null, new Date()));
        user.addReview(newReview);
        review.addReview(newReview);
        return new ReviewQuestionResponseDTO(
                newReview.getId(),
                user.getUsername(),
                newReview.getCreatedAt(),
                newReview.getContent(),
                null,
                0,
                0,
                List.of()
        );
    }

    @Transactional
    public void remove(ReviewRemoveDTO reviewRemoveDTO) {
        var userFind = userRepository.findById(reviewRemoveDTO.userId());
        if (userFind.isEmpty()) {
            throw HttpException.notFound("User not exist");
        }
        var reviewFind = reviewRepository.findById(reviewRemoveDTO.reviewId());
        if (reviewFind.isEmpty()) {
            throw HttpException.notFound("Review not exist");
        }
        var user = userFind.get();
        var review = reviewFind.get();

        if (user.getRole() != Role.ADMIN && !userRepository.containsReview(user.getId(), review)) {
            throw HttpException.unauthorized("Not your review");
        }

        reviewVoteRepository.deleteAll(reviewVoteRepository.findAllVoteByReviewId(review.getId()));
        removeReviewsChildren(review);
        if (review.getParentReview() != null) {
            review.getParentReview().removeReview(review);
        } else {
            var questionOptional = questionRepository.findByReviewIdWithReviews(review);
            if (questionOptional.isEmpty()) {
                throw HttpException.notFound("Review have no question");
            }
            var question = questionOptional.get();
            question.removeReview(review);
        }
        reviewRepository.delete(review);
    }

    private void removeReviewsChildren(Review review) {
        reviewVoteRepository.deleteAll(reviewVoteRepository.findAllVoteByReviewId(review.getId()));
        review.getAuthor().removeReview(review);

        for (var r : review.getReviews()) {
            removeReviewsChildren(r);
        }
    }

    @Transactional
    public void vote(long authorId, long reviewId, boolean isUpVote) {
        var user = userRepository.findById(authorId).orElseThrow(() -> HttpException.notFound("The user does not exists"));
        var review = reviewRepository.findById(reviewId).orElseThrow(() -> HttpException.notFound("the review does not exists"));
        var vote = (isUpVote) ? ReviewVote.upvote(user, review) : ReviewVote.downvote(user, review);
        reviewVoteRepository.save(vote);
    }

    @Transactional
    public void cancelVote(long authorId, long reviewId) {
        var user = userRepository.findById(authorId).orElseThrow(() -> HttpException.notFound("The user does not exists"));
        var review = reviewRepository.findById(reviewId).orElseThrow(() -> HttpException.notFound("the review does not exists"));
        var reviewVoteId = new ReviewVoteId();
        reviewVoteId.setAuthor(user);
        reviewVoteId.setReview(review);
        reviewVoteRepository.deleteById(reviewVoteId);
    }

    @Transactional
    public List<ReviewResponseChildrenDTO> findReviewsByQuestionId(long questionId) {
        var question = questionRepository.findByIdWithReviews(questionId)
                .orElseThrow(() -> HttpException.notFound("This question does not exists"));
        var author = question.getAuthor();
        return question.getReviews().stream().map(review -> {
            var fileContent = new String(question.getFile(), StandardCharsets.UTF_8).split("\n");
            var lineStart = review.getLineStart();
            var lineEnd = review.getLineEnd();
            var citedCode = (lineStart == null || lineEnd == null) ? null : Arrays.stream(fileContent, lineStart - 1, lineEnd)
                    .collect(Collectors.joining("\n"));

            return new ReviewResponseChildrenDTO(review.getId(), author.getUsername(), review.getContent(), citedCode, reviewVoteRepository.findUpvoteNumberByReviewId(review.getId()), reviewVoteRepository.findDownvoteNumberByReviewId(review.getId()), review.getCreatedAt(), getReviews(review.getId()));
        }).toList();
    }

    @Transactional
    public DetailReviewResponseDTO findDetailFromReviewId(long userId, long reviewId) {
        return findDetailsFromReviewIdWithChildren(userId, reviewId);
    }

    private DetailReviewResponseDTO findDetailsFromReviewIdWithChildren(long userId, long reviewId) {
        var review = reviewRepository.findByIdWithReviews(reviewId).orElseThrow(() -> HttpException.notFound("This review does not exists"));

        String citedCode = null;
        if (review.getQuestion() != null) {
            var fileContent = new String(review.getQuestion().getFile(), StandardCharsets.UTF_8).split("\n");
            var lineStart = review.getLineStart();
            var lineEnd = review.getLineEnd();
            citedCode = (lineStart == null || lineEnd == null) ? null : Arrays.stream(fileContent, lineStart - 1, lineEnd)
                    .collect(Collectors.joining("\n"));
        }
        var doesUserVote = reviewVoteRepository.existsReviewVoteByReviewVoteId_Author_IdAndReviewVoteId_Review_Id(userId, reviewId);
        return new DetailReviewResponseDTO(
                review.getId(),
                review.getAuthor().getUsername(),
                review.getCreatedAt(),
                review.getContent(),
                citedCode,
                reviewVoteRepository.findUpvoteNumberByReviewId(review.getId()),
                reviewVoteRepository.findDownvoteNumberByReviewId(review.getId()),
                doesUserVote, //TODO handle vote review from another user
                review.getReviews().stream().map(childReview -> findDetailsFromReviewIdWithChildren(userId, childReview.getId())).toList()
        );
    }
}
