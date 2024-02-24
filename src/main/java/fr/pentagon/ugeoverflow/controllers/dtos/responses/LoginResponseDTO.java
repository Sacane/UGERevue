package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import java.util.Objects;

public record LoginResponseDTO(String username) {
  public LoginResponseDTO {
    Objects.requireNonNull(username);
  }
}
