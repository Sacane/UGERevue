package fr.pentagon.ugeoverflow.controller;

import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.CredentialsDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.controllers.rest.LoginController;
import fr.pentagon.ugeoverflow.service.UserService;
import fr.pentagon.ugeoverflow.testutils.LoginTestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@Import(DatasourceTestConfig.class)
public class LoginControllerTest {
  @Autowired
  private LoginController loginController;

  @Autowired
  private UserService userService;

  @Autowired
  private LoginTestService loginTestService;

  @Test
  @DisplayName("Case of successful authentification")
  void testAuthUser() throws Exception {
    var credentialsDTO = new CredentialsDTO("login", "password");
    userService.register(new UserRegisterDTO("Verestah1", "verestah.fake@gmail.com", "login", "password"));
    loginTestService.login(credentialsDTO)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Verestah1"))
        .andDo(print());
  }

  @Test
  @DisplayName("Case of exception : login not found")
  void loginNotFoundUserAuth() throws Exception {
    var credentialsDTO = new CredentialsDTO("login", "password");
    loginTestService.login(credentialsDTO)
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andDo(print());
  }

  @Test
  @DisplayName("Case of exception : password doesn't match")
  void passwordDoesntMatchUserAuth() throws Exception {
    userService.register(new UserRegisterDTO("verestah1", "verestah@gmail.com", "login1231", "password1"));
    var credentialsDTO = new CredentialsDTO("login", "passwordfazdfa");
    loginTestService.login(credentialsDTO)
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andDo(print());
  }
}
