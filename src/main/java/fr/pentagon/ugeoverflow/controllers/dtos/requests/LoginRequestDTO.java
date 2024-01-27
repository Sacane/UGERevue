package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import java.util.Objects;

public record LoginRequestDTO(String email, String password) {
  public LoginRequestDTO {
    Objects.requireNonNull(email);
    Objects.requireNonNull(password);
  }
}
