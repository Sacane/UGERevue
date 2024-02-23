package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserFollowInfoDTO(@NotNull @NotBlank String username, boolean isFollowing, long id) {
}
