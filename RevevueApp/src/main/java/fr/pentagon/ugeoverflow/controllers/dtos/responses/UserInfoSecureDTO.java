package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import fr.pentagon.ugeoverflow.config.authorization.Role;
import fr.pentagon.ugeoverflow.model.User;

import java.util.Objects;

public record UserInfoSecureDTO(String username, Role role) {
  public UserInfoSecureDTO {
    Objects.requireNonNull(username);
    Objects.requireNonNull(role);
  }

  public static UserInfoSecureDTO from(User user) {
    return new UserInfoSecureDTO(user.getUsername(), user.getRole());
  }
}
