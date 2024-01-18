package fr.pentagon.ugeoverflow.Repository;

import fr.pentagon.ugeoverflow.Model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByLogin(String login);
}
