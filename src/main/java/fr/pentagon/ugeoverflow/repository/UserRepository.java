package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByLogin(String login);
    @Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.questions WHERE u.id = ?1")
    Optional<User> findByIdWithQuestions(long id);
    @Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.reviews WHERE u.id = ?1")
    Optional<User> findByIdWithReviews(long id);
}
