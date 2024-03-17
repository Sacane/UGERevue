package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import java.util.Objects;

public record LoginResponseDTO(String username, String role, String displayName) {
  public LoginResponseDTO {
    Objects.requireNonNull(username);
    Objects.requireNonNull(role);
  }
}
