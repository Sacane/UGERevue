package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

public record QuestionReviewCreateBodyDTO(@Positive long questionId, @NotNull @NotBlank String content, @Nullable Integer lineStart, @Nullable Integer lineEnd, @NotNull List<String> tags) {
}
