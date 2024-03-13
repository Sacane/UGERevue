package fr.pentagon.ugeoverflow.controllers.mvc;

import fr.pentagon.ugeoverflow.config.authentication.RevevueUserDetail;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.service.ReviewService;
import fr.pentagon.ugeoverflow.utils.MarkdownRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/light/reviews")
public class MvcReviewController {
    record SubReviewAuthorContent(long id, String author, String content){}
    private final ReviewService reviewService;
    private final MarkdownRenderer markdownRenderer;

    public MvcReviewController(ReviewService reviewService, MarkdownRenderer markdownRenderer) {
        this.reviewService = reviewService;
        this.markdownRenderer = markdownRenderer;
    }

    @GetMapping("/{reviewId}")
    public String detailPage(@PathVariable("reviewId") long reviewId, Model model) {
        Long userId = SecurityContext.authentication().map(RevevueUserDetail::id).orElse(null);
        var reviews = reviewService.findDetailFromReviewId(userId, reviewId);
        model.addAttribute("review", reviews);
        model.addAttribute("content", markdownRenderer.markdownToHtml(reviews.content()));
        model.addAttribute("subReviews", reviews.reviews().stream().map(e -> new SubReviewAuthorContent(e.id(), e.author(), markdownRenderer.markdownToHtml(e.content()))));
        return "pages/reviews/detail";
    }
}
