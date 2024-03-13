package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewResponseChildrenDTO;
import fr.pentagon.ugeoverflow.utils.MarkdownRenderer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewMarkdownService {
    private final ReviewService reviewService;
    private final MarkdownRenderer markdownRenderer;

    public ReviewMarkdownService(ReviewService reviewService, MarkdownRenderer markdownRenderer) {
        this.reviewService = reviewService;
        this.markdownRenderer = markdownRenderer;
    }

    public List<ReviewResponseChildrenDTO> findReviewsByQuestionId(long questionId){
        return reviewService.findReviewsByQuestionId(questionId)
                .stream().map(e -> new ReviewResponseChildrenDTO(e.id(), e.author(), markdownRenderer.markdownToHtml(e.content()), e.citedCode(), e.upvotes(), e.downvotes(), e.creationDate(), e.reviews()))
                .toList();
    }

}
