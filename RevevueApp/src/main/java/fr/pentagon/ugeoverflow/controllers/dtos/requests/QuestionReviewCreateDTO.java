package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QuestionReviewCreateDTO(long userId, long questionId, @NotNull @NotBlank String content,
                                      @Nullable Integer lineStart, @Nullable Integer lineEnd,
                                      List<String> tagList) {
}
