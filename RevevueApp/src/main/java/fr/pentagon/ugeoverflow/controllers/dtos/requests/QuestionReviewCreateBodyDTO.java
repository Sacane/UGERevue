package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record QuestionReviewCreateBodyDTO(long questionId, @NotNull @NotBlank String content, @Nullable Integer lineStart, @Nullable Integer lineEnd) {
}
