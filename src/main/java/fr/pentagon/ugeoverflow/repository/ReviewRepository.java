package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
