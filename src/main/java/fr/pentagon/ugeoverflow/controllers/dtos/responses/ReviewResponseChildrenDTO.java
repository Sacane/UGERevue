package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import java.util.Date;
import java.util.List;

public record ReviewResponseChildrenDTO(
        String author,
        String content,
        String citedCode,
        long upvotes,
        long downvotes,
        Date creationDate,
        List<ReviewResponseDTO> reviews
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
