package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record QuestionCreateDTO(
    long userId,
    @NotNull @NotBlank @Length(max = 255) String title,
    @NotNull @NotBlank String descrition,
    @NotNull byte[] file,
    @Nullable byte[] testFile) {
}
