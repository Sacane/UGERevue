package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import jakarta.annotation.Nullable;

import java.util.Date;
import java.util.List;

public record ReviewQuestionResponseDTO(long id, String author, Date creationDate, String content, @Nullable String citedCode, long upvotes, long downvotes, List<ReviewResponseDTO> reviews) {
}
