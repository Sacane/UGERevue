package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import java.util.Objects;

public record LoginResponseDTO(String email) {
  public LoginResponseDTO {
    Objects.requireNonNull(email);
  }
}
