package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewOnReviewDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewResponseDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
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
}
