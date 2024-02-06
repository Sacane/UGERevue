package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record ReviewCreateDTO(long userId, @NotNull @NotBlank String title, @NotNull byte[] javaFile, @Nullable byte[] testFile) {
}
