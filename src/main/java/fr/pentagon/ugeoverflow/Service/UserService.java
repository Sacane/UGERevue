package fr.pentagon.ugeoverflow.Service;

import fr.pentagon.ugeoverflow.DTO.CredentialsDTO;
import fr.pentagon.ugeoverflow.DTO.UserConnectedDTO;
import fr.pentagon.ugeoverflow.DTO.UserIdDTO;
import fr.pentagon.ugeoverflow.DTO.UserRegisterDTO;
import fr.pentagon.ugeoverflow.Exception.HttpException;
import fr.pentagon.ugeoverflow.Model.User;
import fr.pentagon.ugeoverflow.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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
