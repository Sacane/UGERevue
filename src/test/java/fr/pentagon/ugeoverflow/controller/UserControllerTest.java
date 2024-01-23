package fr.pentagon.ugeoverflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pentagon.ugeoverflow.dto.CredentialsDTO;
import fr.pentagon.ugeoverflow.dto.UserConnectedDTO;
import fr.pentagon.ugeoverflow.dto.UserIdDTO;
import fr.pentagon.ugeoverflow.dto.UserRegisterDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.exception.HttpExceptionHandler;
import fr.pentagon.ugeoverflow.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
public class UserControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new HttpExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Case of successful register")
    void testRegisterUser() throws Exception {
        var userRegisterDTO = new UserRegisterDTO("verestah","verestah@gmail.com","verestah1","12345");
        var expectedResponse = new UserIdDTO(1L, "verestah");
        when(userService.register(userRegisterDTO)).thenReturn(ResponseEntity.ok(expectedResponse));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("verestah"))
                .andDo(print());
    }

    @Test
    @DisplayName("Case of exception : username already exist")
    void registerUserAlreadyExist() throws Exception {
        userService.register(new UserRegisterDTO("verestah1","verestah@gmail.com","login","password"));
        var userRegisterDTO = new UserRegisterDTO("verestah1","mathis@gmail.com", "login","password");
        lenient().when(userService.register(userRegisterDTO)).thenThrow(HttpException.badRequest("User with this username already exist")); //TODO Question : Pk obligatoire d'utiliser lenient(), sinon UnnecessaryStubbingException:
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("User with this username already exist")) //TODO Body censé contenir le msg null
                .andDo(print());;
    }

    @Test
    @DisplayName("Case of successful authentification")
    void testAuthUser() throws Exception {
        var credentialsDTO = new CredentialsDTO("login", "password");
        userService.register(new UserRegisterDTO("verestah1","verestah@gmail.com","login","password"));
        when(userService.check(credentialsDTO)).thenReturn(ResponseEntity.ok(new UserConnectedDTO(1L, "verestah1", "toDo")));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(credentialsDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("verestah1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value("toDo"))
                .andDo(print());
    }

    @Test
    @DisplayName("Case of exception : login not found")
    void loginNotFoundUserAuth() throws Exception {
        var credentialsDTO = new CredentialsDTO("login", "password");
        when(userService.check(credentialsDTO)).thenThrow(HttpException.notFound("User with this login is not found"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentialsDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("User with this login is not found"))
                .andDo(print());
    }

    @Test
    @DisplayName("Case of exception : password doesn't match")
    void passwordDoesntMatchUserAuth() throws Exception {
        userService.register(new UserRegisterDTO("verestah1","verestah@gmail.com","login","password1"));
        var credentialsDTO = new CredentialsDTO("login", "password");
        when(userService.check(credentialsDTO)).thenThrow(HttpException.unauthorized("Password entered doesn't match"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentialsDTO)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().string("Password entered doesn't match"))
                .andDo(print());
    }
}
