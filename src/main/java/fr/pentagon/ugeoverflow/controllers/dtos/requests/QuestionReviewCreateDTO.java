package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuestionReviewCreateDTO(long userId, long questionId, @NotNull @NotBlank String content,
                                      @Nullable Integer lineStart, @Nullable Integer lineEnd) {
}
