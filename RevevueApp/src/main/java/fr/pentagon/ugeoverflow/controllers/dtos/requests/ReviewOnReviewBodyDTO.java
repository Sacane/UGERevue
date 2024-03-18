package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public record ReviewOnReviewBodyDTO(long reviewId, @NotNull @NotBlank String content, @NotNull List<String> tagList) {
}
