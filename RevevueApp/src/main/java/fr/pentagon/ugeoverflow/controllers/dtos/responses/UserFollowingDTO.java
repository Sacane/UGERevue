package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import java.util.Objects;

public record UserFollowingDTO(long id, String username) {
  public UserFollowingDTO {
    Objects.requireNonNull(username);
    if (id < 0) {
      throw new IllegalArgumentException("id must be positive");
    }
  }
}
