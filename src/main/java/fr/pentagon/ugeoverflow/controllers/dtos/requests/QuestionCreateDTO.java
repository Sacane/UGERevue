package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record QuestionCreateDTO(long userId, @NotNull @NotBlank String title, @NotNull @NotBlank String descrition, @NotNull byte[] file, @Nullable byte[] testFile) {
}
