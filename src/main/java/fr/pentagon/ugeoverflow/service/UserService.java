package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserIdDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder){
        this.repository = Objects.requireNonNull(repository);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
    }

    @Transactional
    public ResponseEntity<UserIdDTO> register(UserRegisterDTO userDTO){
        if(repository.existsByUsername(userDTO.username())){
            throw HttpException.badRequest("User with this username already exist");
        }
        var newUser = repository.save(new User(userDTO.username(),
                userDTO.login(),
                passwordEncoder.encode(userDTO.password()),
                userDTO.email()
                ));
        return ResponseEntity.ok(new UserIdDTO(newUser.getId(), newUser.getUsername()));
    }
}
