package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record QuestionReviewCreateBodyDTO(@Positive long questionId, @NotNull @NotBlank String content,
                                          @Nullable @Positive Integer lineStart, @Nullable @Positive Integer lineEnd,
                                          List<String> tags) {
  public QuestionReviewCreateBodyDTO {
    if (tags == null) {
      tags = List.of();
    }
  }
}
