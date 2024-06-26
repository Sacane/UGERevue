package fr.pentagon.ugeoverflow.controller;

import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.config.authentication.RevevueUserDetail;
import fr.pentagon.ugeoverflow.config.security.AuthenticationChecker;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.CredentialsDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.UserService;
import fr.pentagon.ugeoverflow.testutils.LoginTestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(DatasourceTestConfig.class)
public class LoginControllerTest {
  @Autowired
  private UserService userService;

  @Autowired
  private LoginTestService loginTestService;
  @Autowired
  private UserRepository userRepository;

  @AfterEach
  void afterEach() {
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("Case of successful authentification")
  void testAuthUser() throws Exception {
    var credentialsDTO = new CredentialsDTO("login", "password");
    userService.register(new UserRegisterDTO("Verestah1", "verestah.fake@gmail.com", "login", "password"));
    loginTestService.login(credentialsDTO)
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("login"))
        .andDo(print());
    Optional<RevevueUserDetail> authentication = AuthenticationChecker.authentication();
    assertTrue(authentication.isPresent());
    var auth = authentication.get();
    assertEquals("login", auth.getUsername());
  }

  @Test
  @DisplayName("Case of exception : login unauthorized")
  void loginNotFoundUserAuth() throws Exception {
    var credentialsDTO = new CredentialsDTO("login", "password");
    loginTestService.login(credentialsDTO)
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }

  @Test
  @DisplayName("Case of exception : password doesn't match")
  void passwordDoesntMatchUserAuth() throws Exception {
    userService.register(new UserRegisterDTO("verestah1", "verestah@gmail.com", "login1231", "password1"));
    var credentialsDTO = new CredentialsDTO("login", "passwordfazdfa");
    loginTestService.login(credentialsDTO)
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }

  @Test
  @DisplayName("Successful logout")
  void logoutTest() throws Exception {
    userService.register(new UserRegisterDTO("verestah1", "verestah@gmail.com", "login1231", "password1"));
    var credentialsDTO = new CredentialsDTO("login1231", "password1");
    loginTestService.login(credentialsDTO)
        .andExpect(status().isOk())
        .andDo(print());
    assertTrue(AuthenticationChecker.authentication().isPresent());
    loginTestService.logout()
        .andExpect(status().isOk());
    assertTrue(AuthenticationChecker.authentication().isEmpty());
  }

  @Test
  @DisplayName("Case of exception : Null value given")
  void nullValueAuth() throws Exception{
    userService.register(new UserRegisterDTO("verestah", "verestah@gmail.com", "login1231", "password1"));
    var credentialsDTO = new CredentialsDTO(null, "password1");
    loginTestService.login(credentialsDTO)
            .andExpect(status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                    .value("Invalid request content."))
            .andDo(print());
  }

  @Test
  @DisplayName("Case of exception : blank value given")
  void blankValueAuth() throws Exception{
    userService.register(new UserRegisterDTO("verestah", "verestah@gmail.com", "login1231", "password1"));
    var credentialsDTO = new CredentialsDTO("", "password1");
    loginTestService.login(credentialsDTO)
            .andExpect(status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.detail")
                    .value("Invalid request content."))
            .andDo(print());
  }
}
