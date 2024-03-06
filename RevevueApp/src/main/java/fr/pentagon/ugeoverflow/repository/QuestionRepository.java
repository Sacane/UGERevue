package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = "SELECT q FROM Question q LEFT JOIN FETCH q.reviews WHERE q.id = :questionId")
    Optional<Question> findByIdWithReviews(@Param("questionId") long id);
    @Query(value = "SELECT q FROM Question q LEFT JOIN FETCH q.reviews WHERE ?1 MEMBER OF q.reviews")
    Optional<Question> findByReviewIdWithReviews(Review review);

    @Query(value = "SELECT q FROM Question q WHERE q.author.id = :userId")
    List<Question> findAllByAuthor(@Param("userId") long userId);

    @Query(value = "SELECT q FROM Question q LEFT JOIN FETCH q.author LEFT JOIN FETCH q.reviews")
    List<Question> findAllWithAuthors();

    @Query(value = "SELECT q FROM Question q LEFT JOIN FETCH q.reviews")
    List<Question> findAllWithReviews();

    @Query(value = "SELECT q FROM Question q LEFT JOIN FETCH q.author LEFT JOIN FETCH q.reviews WHERE q.id = :questionId")
    Optional<Question> findByIdWithAuthorAndReviews(@Param("questionId") long questionId);
}
