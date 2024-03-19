package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;

public record QuestionUpdateDTO(@Nullable String description, @Nullable byte[] testFile, String testFilename) {
}
