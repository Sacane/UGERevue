package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import jakarta.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

public record QuestionUpdateDTO(@Nullable String description, @Nullable byte[] testFile, String testFilename) {
}
