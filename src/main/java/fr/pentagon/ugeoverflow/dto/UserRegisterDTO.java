package fr.pentagon.ugeoverflow.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record UserRegisterDTO(@NotNull @NotBlank String username, @NotNull @NotBlank String email, @NotNull @NotBlank String login, @NotNull @NotBlank String password) { }
