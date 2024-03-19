package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import jakarta.annotation.Nullable;

public record QuestionUpdateResponseDTO(@Nullable String description, @Nullable String testFile, @Nullable String testFileResult) {
}
