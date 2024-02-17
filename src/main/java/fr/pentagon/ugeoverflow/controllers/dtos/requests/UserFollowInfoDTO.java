package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record UserFollowInfoDTO(@NotNull @NotBlank String username, boolean isFollowing) {}
