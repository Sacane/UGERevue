package fr.pentagon.ugeoverflow.repository;

import fr.pentagon.ugeoverflow.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByLogin(String login);
}
