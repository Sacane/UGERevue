package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.dto.CredentialsDTO;
import fr.pentagon.ugeoverflow.dto.UserConnectedDTO;
import fr.pentagon.ugeoverflow.dto.UserIdDTO;
import fr.pentagon.ugeoverflow.dto.UserRegisterDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;
import java.util.logging.Logger;

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
                passwordEncoder.encodePassword(userDTO.password()),
                userDTO.email()
                ));
        return ResponseEntity.ok(new UserIdDTO(newUser.getId(), newUser.getUsername()));
    }

    @Transactional
    public ResponseEntity<UserConnectedDTO> check(CredentialsDTO credentialsDTO){
        var user = repository.findByLogin(credentialsDTO.login());
        if(user.isEmpty()){
            throw HttpException.notFound("User with this login is not found");
        }
        var userData = user.get();
        if(!passwordEncoder.verifyPassword(credentialsDTO.password(), userData.getPassword())){
            throw HttpException.unauthorized("Password entered doesn't match");
        }
        return ResponseEntity.ok(new UserConnectedDTO(userData.getId(), userData.getUsername(), "ToDo"));
    }
}
