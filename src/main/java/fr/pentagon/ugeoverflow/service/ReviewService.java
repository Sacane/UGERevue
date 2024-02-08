package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionRemoveDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewOnReviewDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewRemoveDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewResponseDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.model.vote.QuestionVote;
import fr.pentagon.ugeoverflow.model.vote.ReviewVote;
import fr.pentagon.ugeoverflow.model.vote.ReviewVoteId;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.ReviewVoteRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReviewService {
    private final QuestionRepository questionRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewVoteRepository reviewVoteRepository;

    public ReviewService(QuestionRepository questionRepository, ReviewRepository reviewRepository, UserRepository userRepository, ReviewVoteRepository reviewVoteRepository) {
        this.questionRepository = questionRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.reviewVoteRepository = reviewVoteRepository;
    }

    @Transactional
    public List<ReviewResponseDTO> getReviews(long reviewId) {
        var reviewOptional = reviewRepository.findByIdWithReviews(reviewId);
        if (reviewOptional.isEmpty()) {
            throw HttpException.notFound("Review not exist");
        }
        var review = reviewOptional.get();
        var author = review.getAuthor();

        return review.getReviews().stream().map(r ->
                new ReviewResponseDTO(author.getId(), author.getUsername(), r.getId(), r.getContent())).toList();
    }

    @Transactional
    public ResponseEntity<Long> addReview(ReviewOnReviewDTO reviewOnReviewDTO) {
        var userFind = userRepository.findById(reviewOnReviewDTO.userId());
        if (userFind.isEmpty()) {
            throw HttpException.notFound("User not exist");
        }
        var reviewFind = reviewRepository.findById(reviewOnReviewDTO.reviewId());
        if (reviewFind.isEmpty()) {
            throw HttpException.notFound("Review not exist");
        }
        var user = userFind.get();
        var review = reviewFind.get();

        var newReview = reviewRepository.save(new Review(reviewOnReviewDTO.content(), null, null, new Date()));
        user.addReview(newReview);
        review.addReview(newReview);

        return ResponseEntity.ok(newReview.getId());
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

        if (!userRepository.containsReview(user.getId(), review)) {
            throw HttpException.unauthorized("Not your review");
        }

        removeReviewsChildren(review);
        if (review.getParentReview() != null) {
            review.getParentReview().removeReview(review);
        }
        else {
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
        review.getAuthor().removeReview(review);

        for(var r: review.getReviews()) {
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
    public void cancelVote(long authorId, long reviewId){
        var user = userRepository.findById(authorId).orElseThrow(() -> HttpException.notFound("The user does not exists"));
        var review = reviewRepository.findById(reviewId).orElseThrow(() -> HttpException.notFound("the review does not exists"));
        var reviewVoteId = new ReviewVoteId();
        reviewVoteId.setAuthor(user);
        reviewVoteId.setReview(review);
        reviewVoteRepository.deleteById(reviewVoteId);
    }
}
