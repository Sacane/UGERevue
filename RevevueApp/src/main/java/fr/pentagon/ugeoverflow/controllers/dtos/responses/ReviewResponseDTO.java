package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import java.util.Date;

public record ReviewResponseDTO(
        String author,
        String content,
        long upvotes,
        long downvotes,
        Date creationDate
) {
}
