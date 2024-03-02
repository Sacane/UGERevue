package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record ReviewOnReviewBodyDTO(long reviewId, @NotNull @NotBlank String content) {
}
