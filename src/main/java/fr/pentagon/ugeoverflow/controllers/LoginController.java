package fr.pentagon.ugeoverflow.controllers;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.LoginRequestDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.LoginResponseDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.repository.UserRepository;
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
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@CrossOrigin
public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
    private final AuthenticationManager authenticationManager;
    private final SecurityContextHolderStrategy contextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final UserRepository userRepository;

    public LoginController(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
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
                contextHolderStrategy.setContext(securityContext);
                this.securityContextRepository.saveContext(securityContext, request, response);
            }

            var userOptional = userRepository.findByLogin(((UserDetails) authentication.getPrincipal()).getUsername());
            if (userOptional.isEmpty()) {
                throw HttpException.badRequest("Unknow username");
            }
            var user = userOptional.get();

            return ResponseEntity.ok(new LoginResponseDTO(user.getUsername(), user.getRole().toString()));
        } catch (AuthenticationException e) {
            throw HttpException.unauthorized("Bad credentials");
        }
    }

    @PostMapping(Routes.Auth.LOGOUT)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.info("try to logout " + authentication);
        if (authentication != null) {
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, authentication);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                LOGGER.info("logout successfully");
            }
        }
    }
}