package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.CredentialsDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.LoginResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.logging.Logger;

@Component
public class LoginManager {
  private static final Logger LOGGER = Logger.getLogger(LoginManager.class.getName());
  private final AuthenticationManager authenticationManager;
  private final SecurityContextHolderStrategy contextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
  private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

  public LoginManager(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  public Optional<LoginResponseDTO> login(CredentialsDTO credentialsDTO, HttpServletRequest request, HttpServletResponse response) {
    LOGGER.info("try to login");
    var token = UsernamePasswordAuthenticationToken.unauthenticated(credentialsDTO.login(), credentialsDTO.password());
    try {
      var authentication = authenticationManager.authenticate(token);
      if (authentication.isAuthenticated()) {
        var securityContext = this.contextHolderStrategy.createEmptyContext();
        securityContext.setAuthentication(authentication);
        contextHolderStrategy.setContext(securityContext);
        this.securityContextRepository.saveContext(securityContext, request, response);
      }
      return Optional.of(new LoginResponseDTO(((UserDetails) authentication.getPrincipal()).getUsername()));
    } catch (AuthenticationException e) {
      return Optional.empty();
    }
  }

  public void logout(HttpServletRequest request, HttpServletResponse response) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    LOGGER.info("try to logout " + authentication);
    if (authentication != null) {
      var logoutHandler = new SecurityContextLogoutHandler();
      logoutHandler.logout(request, response, authentication);
      if (SecurityContextHolder.getContext().getAuthentication() == null) {
        LOGGER.info("logout successfully");
      }
    }
  }
}
