package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserPasswordUpdateDTO(
    @NotNull
    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 3, max = 20)
    String oldPassword,
    @NotNull
    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 3, max = 20)
    String newPassword) {
}
