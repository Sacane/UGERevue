package fr.pentagon.ugeoverflow.Controller;

import fr.pentagon.ugeoverflow.DTO.CredentialsDTO;
import fr.pentagon.ugeoverflow.DTO.UserConnectedDTO;
import fr.pentagon.ugeoverflow.DTO.UserIdDTO;
import fr.pentagon.ugeoverflow.DTO.UserRegisterDTO;
import fr.pentagon.ugeoverflow.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.logging.Logger;

@RestController
//TODO Add request mapping but with which name ?
public final class UserController {
    private final UserService service;
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    public UserController(UserService service){
        this.service = Objects.requireNonNull(service);
    }

    @PostMapping("/register")
    public ResponseEntity<UserIdDTO> registerUser(@RequestBody UserRegisterDTO userRegisterDTO){
        LOGGER.info("Register a new User");
        return service.register(userRegisterDTO);
    }

    @PostMapping("/auth")
    public ResponseEntity<UserConnectedDTO> checkAccess(@RequestBody CredentialsDTO credentialsDTO){
        LOGGER.info("Checking credentials");
        return service.check(credentialsDTO);
    }
}
