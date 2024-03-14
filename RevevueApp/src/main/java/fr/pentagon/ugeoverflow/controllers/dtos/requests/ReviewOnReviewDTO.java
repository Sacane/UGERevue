package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReviewOnReviewDTO(long userId, long reviewId, @NotNull @NotBlank String content, @NotNull List<String> tagList) {
}
