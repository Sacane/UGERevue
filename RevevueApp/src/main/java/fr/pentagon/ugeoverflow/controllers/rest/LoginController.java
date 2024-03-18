package fr.pentagon.ugeoverflow.controllers.rest;

import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.CredentialsDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.LoginResponseDTO;
import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.service.LoginManager;
import fr.pentagon.ugeoverflow.utils.Routes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class LoginController {
  private final LoginManager loginManager;

  public LoginController(LoginManager loginManager) {
    this.loginManager = loginManager;
  }

  @PostMapping(Routes.Auth.LOGIN)
  public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody CredentialsDTO credentialsDTO, HttpServletRequest request, HttpServletResponse response) {
    return ResponseEntity.ok(
        loginManager.login(credentialsDTO, request, response)
            .orElseThrow(() -> HttpException.unauthorized("Bad credentials"))
    );
  }

  @PostMapping(Routes.Auth.LOGOUT)
  @RequireUser
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    loginManager.logout(request, response);
  }
}
