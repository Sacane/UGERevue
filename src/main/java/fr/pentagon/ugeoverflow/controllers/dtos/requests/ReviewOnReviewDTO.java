package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record ReviewOnReviewDTO(long userId, long reviewId, @NotNull @NotBlank String content) {
}
