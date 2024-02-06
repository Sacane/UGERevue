package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value = "SELECT r FROM Review r LEFT JOIN FETCH r.comments WHERE r.id = ?1")
    Optional<Review> findByIdWithComments(long id);
}
