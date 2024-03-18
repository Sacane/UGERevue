package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record QuestionReviewCreateBodyDTO(@Positive long questionId, @NotNull @NotBlank String content,
                                          @Nullable Integer lineStart, @Nullable Integer lineEnd,
                                          @NotNull List<String> tags) {
}
