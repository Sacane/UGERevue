package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuestionCreateDTO(long userId, @NotNull @NotBlank String title, @NotNull @NotBlank String descrition,
                                @NotNull byte[] file, @Nullable byte[] testFile) {
}
