package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByUsername(String username);

  Optional<User> findByLogin(String login);

  @Query("SELECT u.followers FROM User u WHERE u = :user")
  Set<User> findFollowers(User user);

  @Query("SELECT u.follows FROM User u WHERE u = :user")
  Set<User> findFollowing(User user);
}
