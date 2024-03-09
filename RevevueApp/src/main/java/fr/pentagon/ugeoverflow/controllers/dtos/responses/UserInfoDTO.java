package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import fr.pentagon.ugeoverflow.config.authorization.Role;

import java.util.Objects;

public record UserInfoDTO(String username, String login, String email, Role role) {
  public UserInfoDTO {
    Objects.requireNonNull(username);
    Objects.requireNonNull(login);
    Objects.requireNonNull(email);
    Objects.requireNonNull(role);
  }
}
