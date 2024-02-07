package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = "SELECT q FROM Question q LEFT JOIN FETCH q.reviews WHERE q.id = ?1")
    Optional<Question> findByIdWithReviews(long id);
}
