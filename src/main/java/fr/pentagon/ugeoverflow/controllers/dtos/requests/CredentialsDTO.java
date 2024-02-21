package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CredentialsDTO(
    @NotNull @NotBlank(message = "Account name cannot be empty.") String login,
    @NotNull @NotBlank(message = "Password cannot be empty.") String password) {
}
