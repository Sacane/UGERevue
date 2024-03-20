package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record QuestionReviewCreateBodyDTO(
        @Positive long questionId,
        @NotNull @NotBlank String content,
        @Nullable @PositiveOrZero(message = "La ligne de début doit être positive") Integer lineStart,
        @Nullable @PositiveOrZero(message = "La ligne de fin doit être positive") Integer lineEnd,
        List<String> tags) {
  public QuestionReviewCreateBodyDTO {
    if(content != null) content = content.replace("<script>", "");
    if (tags == null) {
      tags = List.of();
    }
  }
}
