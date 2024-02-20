package fr.pentagon.ugeoverflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.controllers.LoginController;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.CredentialsDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.exception.HttpExceptionHandler;
import fr.pentagon.ugeoverflow.service.UserService;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@Import(DatasourceTestConfig.class)
public class LoginControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private LoginController loginController;
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(loginController)
                .setControllerAdvice(new HttpExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Case of successful authentification")
    void testAuthUser() throws Exception {
        var credentialsDTO = new CredentialsDTO("login", "password");
        userService.register(new UserRegisterDTO("Verestah1", "verestah.fake@gmail.com", "login", "password"));
        mockMvc.perform(MockMvcRequestBuilders.post(Routes.Auth.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentialsDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Verestah1"))
                .andDo(print());
    }

    @Test
    @DisplayName("Case of exception : login not found")
    void loginNotFoundUserAuth() throws Exception {
        var credentialsDTO = new CredentialsDTO("login", "password");
        mockMvc.perform(MockMvcRequestBuilders.post(Routes.Auth.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentialsDTO)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("Case of exception : password doesn't match")
    void passwordDoesntMatchUserAuth() throws Exception {
        userService.register(new UserRegisterDTO("verestah1","verestah@gmail.com","login1231","password1"));
        var credentialsDTO = new CredentialsDTO("login", "passwordfazdfa");
        mockMvc.perform(MockMvcRequestBuilders.post(Routes.Auth.LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentialsDTO)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andDo(print());
    }
}
