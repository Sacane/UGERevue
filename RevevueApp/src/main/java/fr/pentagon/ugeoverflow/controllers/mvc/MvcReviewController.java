package fr.pentagon.ugeoverflow.controllers.mvc;

import fr.pentagon.ugeoverflow.config.authentication.RevevueUserDetail;
import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateBodyDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateDTO;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.service.ReviewService;
import fr.pentagon.ugeoverflow.utils.MarkdownRenderer;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
@RequestMapping("/light/reviews")
public class MvcReviewController {
  private final ReviewService reviewService;
  private final QuestionService questionService;
  private final MarkdownRenderer markdownRenderer;
  public MvcReviewController(ReviewService reviewService, MarkdownRenderer markdownRenderer, QuestionService questionService) {
    this.reviewService = reviewService;
    this.markdownRenderer = markdownRenderer;
    this.questionService = questionService;
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

  @GetMapping("/create/{questionId}")
  @RequireUser
  public String createReviewPage(@ModelAttribute("body") ReviewBodyDTO questionReviewCreateDTO, @PathVariable("questionId") long questionId, Model model) {
    model.addAttribute("request", new QuestionReviewCreateBodyDTO(questionId, questionReviewCreateDTO.content(), questionReviewCreateDTO.lineStart(), questionReviewCreateDTO.lineEnd(), questionReviewCreateDTO.tags()));
    return "pages/reviews/create";
  }

  @PostMapping("/create")
  @RequireUser
  public String createReview(@ModelAttribute("request") QuestionReviewCreateBodyDTO questionReviewCreateDTO) {
    var currentUser = SecurityContext.checkAuthentication();
    questionService.addReview(new QuestionReviewCreateDTO(currentUser.id(), questionReviewCreateDTO.questionId(), questionReviewCreateDTO.content(), questionReviewCreateDTO.lineStart(), questionReviewCreateDTO.lineEnd(), questionReviewCreateDTO.tags() == null ? List.of() : questionReviewCreateDTO.tags()));
    return "redirect:../../light/questions/" + questionReviewCreateDTO.questionId();
  }

  public record SubReviewAuthorContent(long id, String author, String content) {
  }

  public record ReviewBodyDTO(@NotNull @NotBlank String content, @Nullable Integer lineStart, @Nullable Integer lineEnd,
                              List<String> tags) {
  }
}
