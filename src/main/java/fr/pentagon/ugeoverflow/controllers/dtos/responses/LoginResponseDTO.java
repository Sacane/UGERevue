package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import java.util.Objects;

public record LoginResponseDTO(String login) {
  public LoginResponseDTO {
    Objects.requireNonNull(login);
  }
}
