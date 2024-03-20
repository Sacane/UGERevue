package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReviewOnReviewBodyDTO(long reviewId, @NotNull @NotBlank String content, List<String> tagList) {
  public ReviewOnReviewBodyDTO {
    if (tagList == null) {
      tagList = List.of();
    }
  }
}
