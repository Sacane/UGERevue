package fr.pentagon.ugeoverflow.controllers;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.LoginRequestDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.LoginResponseDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.utils.Routes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class LoginController {
  private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
  private final AuthenticationManager authenticationManager;
  private final SecurityContextHolderStrategy contextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
  private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

  public LoginController(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @PostMapping(Routes.Auth.LOGIN)
  public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request, HttpServletResponse response) {
    LOGGER.info("try to login");
    var token = UsernamePasswordAuthenticationToken.unauthenticated(loginRequestDTO.login(), loginRequestDTO.password());
    try {
      var authentication = authenticationManager.authenticate(token);
      if (authentication.isAuthenticated()) {
        var securityContext = this.contextHolderStrategy.createEmptyContext();
        securityContext.setAuthentication(authentication);
        this.securityContextRepository.saveContext(securityContext, request, response);
      }
      return ResponseEntity.ok(new LoginResponseDTO(((UserDetails) authentication.getPrincipal()).getUsername()));
    } catch (AuthenticationException e) {
      throw HttpException.unauthorized("Bad credentails");
    }
  }

  @PostMapping(Routes.Auth.LOGOUT)
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    LOGGER.info("try to logout");
    if (authentication != null) {
      SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
      logoutHandler.logout(request, response, authentication);
      if (SecurityContextHolder.getContext().getAuthentication() == null) {
        LOGGER.info("logout successfully");
      }
    }
  }
}