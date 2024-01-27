package fr.pentagon.ugeoverflow.controllers;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.LoginRequestDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.LoginResponseDTO;
import fr.pentagon.ugeoverflow.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final SecurityContextHolderStrategy contextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
  private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

  public LoginController(AuthenticationManager authenticationManager, UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
  }

  @PostMapping("/login")
  public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request, HttpServletResponse response) {
    var token = UsernamePasswordAuthenticationToken.unauthenticated(loginRequestDTO.email(), loginRequestDTO.password());
    var authentication = authenticationManager.authenticate(token);

    // The authentication must be manually saved into the SecurityContext
    if (authentication.isAuthenticated()) {
      var securityContext = this.contextHolderStrategy.createEmptyContext();
      securityContext.setAuthentication(authentication);
      this.securityContextRepository.saveContext(securityContext, request, response);
    }
    return new LoginResponseDTO(authentication.getName());
  }
}