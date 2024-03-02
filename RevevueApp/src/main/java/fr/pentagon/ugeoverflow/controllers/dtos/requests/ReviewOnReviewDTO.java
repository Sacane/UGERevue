package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewOnReviewDTO(long userId, long reviewId, @NotNull @NotBlank String content) {
}
