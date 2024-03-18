package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.validation.constraints.NotNull;

public record VoteBodyDTO(@NotNull boolean up) {
}
