package fr.pentagon.ugeoverflow.Service;

import fr.pentagon.ugeoverflow.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public final class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository){
        this.repository = Objects.requireNonNull(repository);
    }
}
