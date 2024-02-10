package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;

public record QuestionUpdateDTO(long userId, long questionId, @Nullable String title, @Nullable String description, @Nullable byte[] file, @Nullable byte[] testFile) {
}
