package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import jakarta.annotation.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public record ReviewResponseDTO(long authorId, @NotNull @NotBlank String authorName, long reviewId, @NotNull @NotBlank String content, @Nullable String citedCode, List<ReviewResponseDTO> reviews) {
}
