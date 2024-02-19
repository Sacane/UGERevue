package fr.pentagon.ugeoverflow.controller;

import fr.pentagon.ugeoverflow.DatasourceTestConfig;
import fr.pentagon.ugeoverflow.controllers.UserController;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@Import(DatasourceTestConfig.class)
public class UserControllerFollowTest {
  @Autowired
  private UserController userController;
  @Autowired
  private UserRepository userRepository;
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(userController)
        .setControllerAdvice(new HttpExceptionHandler())
        .build();
  }

  @AfterEach
  void clean() {
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("Successful follow")
  @WithMockUser
  void testSuccessfulFollow() throws Exception {
    var userDTO = userService.register(new UserRegisterDTO("test1", "test@gmail.com", "test1", "password"));
    var principal = new TestingAuthenticationToken(userDTO.username(), null);
    var userToFollowId = userService.register(new UserRegisterDTO("test2", "test@gmail.com", "test2", "password")).id();

    mockMvc.perform(post("/api/users/follow/" + userToFollowId)
            .principal(principal))
        .andExpect(MockMvcResultMatchers.status().isOk());

    var user = userRepository.findById(userDTO.id()).orElseThrow();
    var userToFollow = userRepository.findById(userToFollowId).orElseThrow();
    assertTrue(userRepository.findFollowers(userToFollow).contains(user));
    assertTrue(userRepository.findFollowing(user).contains(userToFollow));
  }

  @Test
  @DisplayName("Following user that doesn't exist")
  @WithMockUser
  void testFollowNonExistingUser() throws Exception {
    var userDTO = userService.register(new UserRegisterDTO("test1", "test@gmail.com", "test1", "password"));
    var principal = new TestingAuthenticationToken(userDTO.username(), null);

    mockMvc.perform(post(Routes.User.FOLLOW + "/" + 12315631)
            .principal(principal))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @DisplayName("Following while not connected")
  @WithMockUser
  void testFollowNotConnected() throws Exception {
    mockMvc.perform(post(Routes.User.FOLLOW + "/" + 1))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  @DisplayName("Successful unfollow")
  @WithMockUser
  void testSuccessfulUnfollow() throws Exception {
    var userDTO = userService.register(new UserRegisterDTO("test1", "test@gmail.com", "test1", "password"));
    var principal = new TestingAuthenticationToken(userDTO.username(), null);
    var userToFollowId = userService.register(new UserRegisterDTO("test2", "test@gmail.com", "test2", "password")).id();

    userService.follow(userDTO.id(), userToFollowId);

    mockMvc.perform(post(Routes.User.UNFOLLOW + "/" + userToFollowId)
            .principal(principal))
        .andExpect(MockMvcResultMatchers.status().isOk());

    var user = userRepository.findById(userDTO.id()).orElseThrow();
    var userToFollow = userRepository.findById(userToFollowId).orElseThrow();
    assertFalse(userRepository.findFollowers(userToFollow).contains(user));
    assertFalse(userRepository.findFollowing(user).contains(userToFollow));
  }

  @Test
  @DisplayName("Following user that doesn't exist")
  @WithMockUser
  void testUnfollowNonExistingUser() throws Exception {
    var userDTO = userService.register(new UserRegisterDTO("test1", "test@gmail.com", "test1", "password"));
    var principal = new TestingAuthenticationToken(userDTO.username(), null);

    mockMvc.perform(post(Routes.User.UNFOLLOW + "/" + 12315631)
            .principal(principal))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @DisplayName("Following while not connected")
  @WithMockUser
  void testUnfollowNotConnected() throws Exception {
    mockMvc.perform(post(Routes.User.UNFOLLOW + "/" + 1))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }
}
