package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record ReviewResponseDTO(long authorId, @NotNull @NotBlank String authorName, long reviewId, @NotNull @NotBlank String content) {
}
