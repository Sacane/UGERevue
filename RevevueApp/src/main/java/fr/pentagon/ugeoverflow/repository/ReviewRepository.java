package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ReviewRepository extends JpaRepository<Review, Long> {
  @Query(value = "SELECT r FROM Review r LEFT JOIN FETCH r.reviews LEFT JOIN FETCH r.parentReview WHERE r.id = ?1")
  Optional<Review> findByIdWithReviews(long id);

  @Query(value = "SELECT r FROM Review r LEFT JOIN FETCH r.question WHERE r.id = ?1")
  Optional<Review> findByIdWithQuestion(long id);

  @Query(value = "SELECT r FROM Review r LEFT JOIN FETCH r.tagsList WHERE r.id = ?1")
  Optional<Review> findByIdWithTags(long id);

  @Query("SELECT r FROM Review r LEFT JOIN FETCH r.tagsList t WHERE t.name = :tagName")
  Set<Review> findByTagName(@Param("tagName") String tagName);

  @Query(value = "SELECT r FROM Review r LEFT JOIN FETCH r.tagsList t LEFT JOIN FETCH r.question WHERE r.author.id = :userId")
  List<Review> withTagsAndQuestion(@Param("userId") long userId);

  List<Review> findByAuthorAndQuestionIsNotNullOrderByCreatedAtDesc(User author);
}
