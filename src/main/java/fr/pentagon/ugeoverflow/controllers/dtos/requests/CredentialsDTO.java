package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record CredentialsDTO(@NotNull @NotBlank String login, @NotNull @NotBlank String password) { }