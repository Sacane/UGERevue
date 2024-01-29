package fr.pentagon.ugeoverflow.controllers;

import fr.pentagon.ugeoverflow.controllers.dtos.responses.UserIdDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/users")
public final class UserController {
    private final UserService service;
    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());

    public UserController(UserService service){
        this.service = Objects.requireNonNull(service);
    }

    @PostMapping
    public ResponseEntity<UserIdDTO> registerUser(@RequestBody UserRegisterDTO userRegisterDTO){
        return service.register(userRegisterDTO);
    }
}
