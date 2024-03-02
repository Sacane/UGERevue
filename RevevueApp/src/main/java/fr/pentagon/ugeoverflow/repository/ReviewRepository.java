package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value = "SELECT r FROM Review r LEFT JOIN FETCH r.reviews LEFT JOIN FETCH r.parentReview WHERE r.id = ?1")
    Optional<Review> findByIdWithReviews(long id);
    @Query(value = "SELECT r FROM Review r LEFT JOIN FETCH r.question WHERE r.id = ?1")
    Optional<Review> findByIdWithQuestion(long id);
}
