package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.questions WHERE u.id = ?1")
    Optional<User> findByIdWithQuestions(long id);
    @Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.reviews WHERE u.id = ?1")
    Optional<User> findByIdWithReviews(long id);
    @Query(value = "SELECT EXISTS (SELECT u FROM User u LEFT JOIN u.questions WHERE u.id = ?1 AND ?2 MEMBER OF u.questions)")
    boolean containsQuestion(long userId, Question question);
    @Query(value = "SELECT EXISTS (SELECT u FROM User u LEFT JOIN u.reviews WHERE u.id = ?1 AND ?2 MEMBER OF u.reviews)")
    boolean containsReview(long userId, Review review);
    boolean existsByUsername(String username);
    boolean existsByUsernameOrLogin(String username, String login);
    Optional<User> findByLogin(String login);
    @Query("SELECT u.followers FROM User u WHERE u = :user")
    Set<User> findFollowers(User user);
    @Query("SELECT u.follows FROM User u WHERE u = :user")
    Set<User> findFollowing(User user);
    @Query("SELECT u FROM User u")
    Set<User> findAllUsers();
    @Query("SELECT u.follows FROM User u WHERE u.id = :id")
    Set<User> findFollowsById(long id);
}
