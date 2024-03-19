package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.Tag;
import fr.pentagon.ugeoverflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.questions WHERE u.id = :id")
    Optional<User> findByIdWithQuestions(@Param("id") long id);
    @Query(value = "SELECT u FROM User u LEFT JOIN FETCH u.reviews WHERE u.id = :id")
    Optional<User> findByIdWithReviews(@Param("id") long id);
    @Query(value = "SELECT EXISTS (SELECT u FROM User u LEFT JOIN u.questions WHERE u.id = ?1 AND ?2 MEMBER OF u.questions)")
    boolean containsQuestion(long userId, Question question);
    @Query(value = "SELECT EXISTS (SELECT u FROM User u LEFT JOIN u.reviews WHERE u.id = :userId AND :review MEMBER OF u.reviews)")
    boolean containsReview(@Param("userId") long userId, @Param("review") Review review);
    boolean existsByUsername(String username);
    boolean existsByUsernameOrLogin(String username, String login);
    Optional<User> findByLogin(String login);
    @Query("SELECT u.followers FROM User u WHERE u = :user")
    Set<User> findFollowers(@Param("user") User user);
    @Query("SELECT u.followers FROM User u WHERE u.id = :userId")
    List<User> findFollowsByUserId(@Param("userId") long userId);
    @Query("SELECT u.follows FROM User u WHERE u = :user")
    Set<User> findFollowing(@Param("user") User user);
    @Query("SELECT u FROM User u")
    Set<User> findAllUsers();
    @Query("SELECT u.follows FROM User u WHERE u.id = :id")
    Set<User> findFollowsById(@Param("id") long id);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.tagsCreated t WHERE t.name = :tagName")
    Set<User> findByTagName(@Param("tagName") String tagName);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.tagsCreated WHERE u.id = :id")
    Optional<User> findByIdWithTag(@Param("id") long id);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u LEFT JOIN u.reviews r JOIN r.tagsList t WHERE u.id = :userId AND t.id = :tagId")
    boolean hasReviewWithTag(@Param("userId") long userId, @Param("tagId") long tagId);

    Optional<User> findByUsername(String username);
}
