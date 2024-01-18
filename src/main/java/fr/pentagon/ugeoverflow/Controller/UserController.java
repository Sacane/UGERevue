package fr.pentagon.ugeoverflow.Controller;

import fr.pentagon.ugeoverflow.Service.UserService;

import java.util.Objects;

public final class UserController {

    private final UserService service;

    public UserController(UserService service){
        this.service = Objects.requireNonNull(service);
    }

    //TODO Ajouter la route pour register un user
    //TODO Configurer la classe comme un REST controller
}
