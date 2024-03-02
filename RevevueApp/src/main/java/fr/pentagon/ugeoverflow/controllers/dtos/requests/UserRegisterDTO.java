package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRegisterDTO(
    @NotNull
    @NotBlank(message = "Display name cannot be blank.")
    @Size(min = 3, max = 20)
    String username,
    @NotNull @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email must be valid.", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    String email,
    @NotNull
    @NotBlank(message = "Account name cannot be blank.")
    @Size(min = 3, max = 20) String login,
    @NotNull
    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 3, max = 20) String password) {
}
