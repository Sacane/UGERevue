package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import jakarta.annotation.Nullable;

import java.util.Date;
import java.util.List;

public record DetailReviewResponseDTO(long id, String author, Date creationDate, String content, @Nullable String citedCode, long upvotes, long downvotes, @Nullable Boolean vote, List<DetailReviewResponseDTO> reviews, List<String> tags, Integer lineStart, Integer lineEnd) {
}
