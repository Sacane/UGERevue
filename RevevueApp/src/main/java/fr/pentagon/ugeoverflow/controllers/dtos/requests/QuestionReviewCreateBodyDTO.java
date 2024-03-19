package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record QuestionReviewCreateBodyDTO(@Positive long questionId, @NotNull @NotBlank String content,
                                          @Nullable @PositiveOrZero Integer lineStart, @Nullable @PositiveOrZero Integer lineEnd,
                                          List<String> tags) {
  public QuestionReviewCreateBodyDTO {
    if (tags == null) {
      tags = List.of();
    }
  }
}
