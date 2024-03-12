package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewContentDTO(@NotBlank @NotNull String content) {}
