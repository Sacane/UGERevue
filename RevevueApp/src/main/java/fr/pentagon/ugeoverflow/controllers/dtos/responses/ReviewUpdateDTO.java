package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

public record ReviewUpdateDTO(
        @NotNull @NotBlank String content,
        Integer lineStart,
        Integer lineEnd,
        List<String> tags
) {
    public ReviewUpdateDTO{
        Objects.requireNonNull(content);
        if(tags == null) tags = List.of();
    }
}
