package fr.pentagon.ugeoverflow.service;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewOnReviewDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewRemoveDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.*;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.Tag;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.model.embed.CodePart;
import fr.pentagon.ugeoverflow.model.vote.ReviewVote;
import fr.pentagon.ugeoverflow.model.vote.ReviewVoteId;
import fr.pentagon.ugeoverflow.repository.*;
import fr.pentagon.ugeoverflow.service.mapper.ReviewMapper;
import jakarta.transaction.Transactional;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final QuestionRepository questionRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewVoteRepository reviewVoteRepository;
    private final TagService tagService;
    private final ReviewMapper reviewMapper;
    private final Logger logger = Logger.getLogger(ReviewService.class.getName());
    private final TagRepository tagRepository;

    public ReviewService(QuestionRepository questionRepository,
                         ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         ReviewVoteRepository reviewVoteRepository,
                         TagService tagService,
                         ReviewMapper reviewMapper,
                         TagRepository tagRepository) {
        this.questionRepository = questionRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.reviewVoteRepository = reviewVoteRepository;
        this.tagService = tagService;
        this.reviewMapper = reviewMapper;
        this.tagRepository = tagRepository;
    }

    List<ReviewResponseDTO> getReviews(long reviewId) {
        var review = reviewRepository.findByIdWithReviews(reviewId)
                .orElseThrow(() -> HttpException.notFound("Review not exist"));
        var author = review.getAuthor();
        return review.getReviews()
                .stream()
                .map(r -> reviewMapper.entityToReviewResponseDTO(r, author.getUsername()))
                .toList();
    }

    @Transactional
    public ReviewQuestionResponseDTO addReview(ReviewOnReviewDTO reviewOnReviewDTO) {
        var user = userRepository.findById(reviewOnReviewDTO.userId())
                .orElseThrow(() -> HttpException.notFound("User not exist"));
        var review = reviewRepository.findById(reviewOnReviewDTO.reviewId())
                .orElseThrow(() -> HttpException.notFound("Review not exist"));
        var newReview = reviewRepository.save(new Review(reviewOnReviewDTO.content(), null, new Date()));
        user.addReview(newReview);
        review.addReview(newReview);
        tagService.addTag(user, review, reviewOnReviewDTO.tagList());
        return reviewMapper.entityToReviewQuestionResponseDTO(newReview, user.getUsername());
    }


    @Transactional
    public void remove(ReviewRemoveDTO reviewRemoveDTO) {
        var userFind = userRepository.findByIdWithTag(reviewRemoveDTO.userId());
        if (userFind.isEmpty()) {
            logger.severe("review does not exists");
            throw HttpException.notFound("User not exist");
        }
        var reviewFind = reviewRepository.findByIdWithTags(reviewRemoveDTO.reviewId());
        if (reviewFind.isEmpty()) {
            logger.severe("Review does not exists");
            throw HttpException.notFound("Review not exist");
        }
        var user = userFind.get();
        Review review = reviewFind.get();

        if (user.getRole() != Role.ADMIN && !userRepository.containsReview(user.getId(), review)) {
            logger.severe("You are trying to delete a review that is not yours");
            throw HttpException.unauthorized("Not your review");
        }

        reviewVoteRepository.deleteAll(reviewVoteRepository.findAllVoteByReviewId(review.getId()));
        removeReviewsChildren(review, user);
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
        removeTagsFromReview(review, user);
        reviewRepository.delete(review);
    }

    private void removeTagsFromReview(Review review, User user) {
        review.getTagsList().forEach(tag -> {
            review.removeTag(tag);
            if (!userRepository.hasReviewWithTag(user.getId(), tag.getId())) {
                user.removeTag(tag);
            }
        });
    }

    public void removeWithoutUser(long reviewId, User user) {
        var reviewFind = reviewRepository.findByIdWithTags(reviewId);
        if (reviewFind.isEmpty()) {
            throw HttpException.notFound("Review not exist");
        }
        Review review = reviewFind.get();

        reviewVoteRepository.deleteAll(reviewVoteRepository.findAllVoteByReviewId(review.getId()));
        review.getAuthor().removeReview(review);
        if (review.getQuestion() != null) {
            review.getQuestion().removeReview(review);
        }
        removeReviewsChildren(review, user);

        reviewRepository.delete(review);
    }

    private void removeReviewsChildren(Review review, User user) {
        reviewVoteRepository.deleteAll(reviewVoteRepository.findAllVoteByReviewId(review.getId()));
        review.getAuthor().removeReview(review);

        for (var r : review.getReviews()) {
            removeTagsFromReview(review, user);
            removeReviewsChildren(r, user);
        }
    }

    @Transactional
    public void vote(long authorId, long reviewId, boolean isUpVote) {
        var user = userRepository.findById(authorId).orElseThrow(() -> HttpException.notFound("The user does not exists"));
        var review = reviewRepository.findById(reviewId).orElseThrow(() -> HttpException.notFound("The review does not exists"));
        var vote = (isUpVote) ? ReviewVote.upvote(user, review) : ReviewVote.downvote(user, review);
        reviewVoteRepository.save(vote);
    }

    @Transactional
    public void cancelVote(long authorId, long reviewId) {
        var user = userRepository.findById(authorId).orElseThrow(() -> HttpException.notFound("The user does not exists"));
        var review = reviewRepository.findById(reviewId).orElseThrow(() -> HttpException.notFound("The review does not exists"));
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
            String citedCode = null;
            var fileContent = new String(question.getFile(), StandardCharsets.UTF_8).split("\n");
            if (review.getCodePart() != null) {
                var lineStart = review.getCodePart().getLineStart();
                var lineEnd = review.getCodePart().getLineEnd();
                citedCode = Arrays.stream(fileContent, lineStart - 1, lineEnd)
                        .collect(Collectors.joining("\n"));
            }
            var start = review.getCodePart() == null ? null : review.getCodePart().getLineStart();
            var end = review.getCodePart() == null ? null : review.getCodePart().getLineEnd();
            return new ReviewResponseChildrenDTO(review.getId(), review.getAuthor().getUsername(), review.getContent(), citedCode, reviewVoteRepository.findUpvoteNumberByReviewId(review.getId()), reviewVoteRepository.findDownvoteNumberByReviewId(review.getId()), review.getCreatedAt(), getReviews(review.getId()), start, end, review.getTagsList().stream().map(Tag::getName).toList());
        }).toList();
    }

    @Transactional
    public DetailReviewResponseDTO findDetailFromReviewId(Long userId, long reviewId) {
        return findDetailsFromReviewIdWithChildren(userId, reviewId);
    }

    private DetailReviewResponseDTO findDetailsFromReviewIdWithChildren(Long userId, long reviewId) {
        var review = reviewRepository.findByIdWithReviews(reviewId).orElseThrow(() -> HttpException.notFound("This review does not exists"));
        String citedCode = null;
        if (review.getQuestion() != null) {
            var fileContent = new String(review.getQuestion().getFile(), StandardCharsets.UTF_8).split("\n");
            if (review.getCodePart() != null) {
                var lineStart = review.getCodePart().getLineStart();
                var lineEnd = review.getCodePart().getLineEnd();
                citedCode = Arrays.stream(fileContent, lineStart - 1, lineEnd)
                        .collect(Collectors.joining("\n"));
            }
        }
        Boolean doesUserVote = null;
        if (userId != null) {
            doesUserVote = reviewVoteRepository.findReviewVote(userId, reviewId);
        }
        var list = review.getReviews().stream().map(childReview -> findDetailsFromReviewIdWithChildren(userId, childReview.getId())).toList();
        return reviewMapper.entityToDetailReviewResponseDTO(review, citedCode, doesUserVote, list);
    }

    @Transactional
    public List<ReviewQuestionTitleDTO> findByTag(String tag) {
        List<Review> reviews = reviewRepository.withTagsAndQuestion(SecurityContext.checkAuthentication().id());
        return reviews.stream().filter(e -> e.getTagsList().stream().anyMatch(reviewTag -> reviewTag.getName().contains(tag)))
                .map(e -> new ReviewQuestionTitleDTO(e.getContent(), e.getQuestion().getTitle()))
                .toList();
    }

    @Transactional
    @Retryable(retryFor = OptimisticEntityLockException.class)
    public ReviewUpdateDTO updateById(long reviewId, ReviewUpdateDTO reviewUpdateDTO) {
        var review = reviewRepository.findByIdWithTags(reviewId)
                .orElseThrow(() -> HttpException.notFound("This review does not exists"));
        var codePart = (reviewUpdateDTO.lineStart() == null || reviewUpdateDTO.lineEnd() == null) ? null : new CodePart(reviewUpdateDTO.lineStart(), reviewUpdateDTO.lineEnd());
        var tags = tagRepository.findAllByNames(reviewUpdateDTO.tags());
        review.getTagsList().clear();
        review.update(reviewUpdateDTO.content(), codePart, new HashSet<>(tags));
        return reviewUpdateDTO;
    }
}
