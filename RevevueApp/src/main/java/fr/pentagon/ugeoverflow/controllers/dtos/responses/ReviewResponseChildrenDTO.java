package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import java.util.Date;
import java.util.List;

public record ReviewResponseChildrenDTO(
        long id,
        String author,
        String content,
        String citedCode,
        long upvotes,
        long downvotes,
        Date creationDate,
        List<ReviewResponseDTO> reviews,
        Integer lineStart,
        Integer lineEnd,
        List<String> tags
) {}
/**
 * export interface Review {
 *     author: string;
 *     creationDate: Date;
 *     content: string;
 *     citedCode?: string;
 *     upvotes: number;
 *     downvotes: number;
 *     reviews: Array<Review>;
 * }
 */
