package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record CommentReviewDTO(@NotNull @NotBlank String content, long authorId, long reviewId) {
}
