package fr.pentagon.ugeoverflow.controller;

import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.CredentialsDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.controllers.rest.UserController;
import fr.pentagon.ugeoverflow.exception.HttpExceptionHandler;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import fr.pentagon.ugeoverflow.service.UserService;
import fr.pentagon.ugeoverflow.testutils.LoginTestService;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@Import(DatasourceTestConfig.class)
public class UserControllerFollowTest {
  @Autowired
  private UserRepository userRepository;
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;
  @Autowired
  private LoginTestService loginTestService;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService, userRepository))
        .setControllerAdvice(new HttpExceptionHandler())
        .build();
  }

  @AfterEach
  void clean() {
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("Successful follow")
  void testSuccessfulFollow() throws Exception {
    var userDTO = userService.register(new UserRegisterDTO("test1", "test@gmail.com", "test1", "password"));
    var userToFollowId = userService.register(new UserRegisterDTO("test2", "test@gmail.com", "test2", "password")).id();
    loginTestService.login(new CredentialsDTO("test1", "password"));

    mockMvc.perform(post("/api/users/follow/" + userToFollowId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());

    var user = userRepository.findById(userDTO.id()).orElseThrow();
    var userToFollow = userRepository.findById(userToFollowId).orElseThrow();
    assertTrue(userRepository.findFollowers(userToFollow).contains(user));
    assertTrue(userRepository.findFollowing(user).contains(userToFollow));
  }

  @Test
  @DisplayName("Following user that doesn't exist")
  void testFollowNonExistingUser() throws Exception {
    userService.register(new UserRegisterDTO("test1", "test@gmail.com", "test1", "password"));
    loginTestService.login(new CredentialsDTO("test1", "password"));

    mockMvc.perform(post(Routes.User.FOLLOW + "/" + 12315631).contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @DisplayName("Following while not connected")
  //@WithMockUser
  void testFollowNotConnected() throws Exception {
    mockMvc.perform(post(Routes.User.FOLLOW + "/" + 1))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  @DisplayName("Successful unfollow")
  void testSuccessfulUnfollow() throws Exception {
    var userDTO = userService.register(new UserRegisterDTO("test1", "test@gmail.com", "test1", "password"));
    var userToFollowId = userService.register(new UserRegisterDTO("test2", "test@gmail.com", "test2", "password")).id();
    loginTestService.login(new CredentialsDTO("test1", "password"));
    userService.follow(userDTO.id(), userToFollowId);

    mockMvc.perform(post(Routes.User.UNFOLLOW + "/" + userToFollowId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());

    var user = userRepository.findById(userDTO.id()).orElseThrow();
    var userToFollow = userRepository.findById(userToFollowId).orElseThrow();
    assertFalse(userRepository.findFollowers(userToFollow).contains(user));
    assertFalse(userRepository.findFollowing(user).contains(userToFollow));
  }

  @Test
  @DisplayName("Following user that doesn't exist")
  void testUnfollowNonExistingUser() throws Exception {
    userService.register(new UserRegisterDTO("test1", "test@gmail.com", "test1", "password"));
    loginTestService.login(new CredentialsDTO("test1", "password"));
    mockMvc.perform(post(Routes.User.UNFOLLOW + "/" + 12315631).contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @DisplayName("Following while not connected")
  void testUnfollowNotConnected() throws Exception {
    mockMvc.perform(post(Routes.User.UNFOLLOW + "/" + 1))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }
}
