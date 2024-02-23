package fr.pentagon.ugeoverflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.config.SetupDataLoader;
import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.controllers.rest.UserController;
import fr.pentagon.ugeoverflow.exception.HttpExceptionHandler;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.UserService;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.junit.jupiter.api.AfterEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@Import(DatasourceTestConfig.class)
public class UserControllerRegisterTest {
  private final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired
  SetupDataLoader setupDataLoader;
  @Autowired
  private UserService userService;
  @Autowired
  private UserController userController;
  @Autowired
  private UserRepository userRepository;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(userController)
        .setControllerAdvice(new HttpExceptionHandler())
        .build();
    setupDataLoader.createAdminIfNotFound();
  }

  @AfterEach
  void clean() {
    userRepository.deleteAll();
  }

  void assertUserHasRole(String login, Role role) {
    var user = userRepository.findByLogin(login).orElseThrow();
    assertEquals(role, user.getRole());
  }

  @Test
  @DisplayName("Check roles of admin account")
  void testAdminUser() {
    assertUserHasRole("admin", Role.ADMIN);
  }

  @Test
  @DisplayName("Case of successful register")
  void testRegisterUser() throws Exception {
    var userRegisterDTO = new UserRegisterDTO("verestah", "verestah@gmail.com", "verestah1", "12345");
    mockMvc.perform(MockMvcRequestBuilders.post(Routes.User.ROOT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userRegisterDTO)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("verestah"))
        .andDo(print())
        .andReturn();

    assertUserHasRole("verestah1", Role.USER);
  }

  @Test
  @DisplayName("Case of exception : username already exist")
  void registerUserAlreadyExist() throws Exception {
    userService.register(new UserRegisterDTO("verestah1", "verestah@gmail.com", "login", "password"));
    var userRegisterDTO = new UserRegisterDTO("verestah1", "mathis@gmail.com", "login", "password");
    mockMvc.perform(MockMvcRequestBuilders.post(Routes.User.ROOT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userRegisterDTO)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().string("User with this username or login already exist"))
        .andDo(print());
  }
}
