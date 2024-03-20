package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import java.util.Date;
import java.util.Objects;

public record UserReviewDTO(long reviewId, String reviewContent, String questionTitle, Date creationDate) {
  public UserReviewDTO {
    if (reviewId < 0) {
      throw new IllegalArgumentException("id can't be negative");
    }
    Objects.requireNonNull(reviewContent);
    Objects.requireNonNull(questionTitle);
    Objects.requireNonNull(creationDate);
  }
}
