package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import javax.validation.constraints.NotNull;

public record VoteBodyDTO(@NotNull boolean up) {
}
