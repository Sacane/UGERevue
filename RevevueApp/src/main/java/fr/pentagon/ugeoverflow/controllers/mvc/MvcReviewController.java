package fr.pentagon.ugeoverflow.controllers.mvc;

import fr.pentagon.ugeoverflow.config.authentication.RevevueUserDetail;
import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.AuthenticationChecker;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateBodyDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewOnReviewBodyDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewOnReviewDTO;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.service.ReviewService;
import fr.pentagon.ugeoverflow.utils.MarkdownRenderer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
  public String detailPage(@PathVariable("reviewId") @Positive long reviewId, Model model) {
    Long userId = AuthenticationChecker.authentication().map(RevevueUserDetail::id).orElse(null);
    var reviews = reviewService.findDetailFromReviewId(userId, reviewId);
    model.addAttribute("review", reviews);
    model.addAttribute("content", markdownRenderer.markdownToHtml(reviews.content()));
    model.addAttribute("subReviews", reviews.reviews().stream().map(e -> new SubReviewAuthorContent(e.id(), e.author(), markdownRenderer.markdownToHtml(e.content()))));
    return "pages/reviews/detail";
  }

  @PostMapping("/upvote/{reviewId}")
  @RequireUser
  public String upvoteReview(@PathVariable("reviewId") @Positive long reviewId) {
    var user = AuthenticationChecker.checkAuthentication();
    reviewService.vote(user.id(), reviewId, true);
    return "pages/return";
  }

  @PostMapping("/downvote/{reviewId}")
  @RequireUser
  public String downvoteReview(@PathVariable("reviewId") @Positive long reviewId) {
    var user = AuthenticationChecker.checkAuthentication();
    reviewService.vote(user.id(), reviewId, false);
    return "pages/return";
  }

  @GetMapping("/create/{questionId}")
  @RequireUser
  public String createReviewPage(@ModelAttribute("body") QuestionReviewCreateBodyDTO questionReviewCreateDTO, @PathVariable("questionId") long questionId, Model model) {
    model.addAttribute("request", new QuestionReviewCreateBodyDTO(questionId, questionReviewCreateDTO.content(), questionReviewCreateDTO.lineStart(), questionReviewCreateDTO.lineEnd(), questionReviewCreateDTO.tags()));
    return "pages/reviews/create";
  }

  @PostMapping("/create")
  @RequireUser
  public String createReview(@Valid @ModelAttribute("request") QuestionReviewCreateBodyDTO questionReviewCreateDTO, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "pages/reviews/create";
    }
    var currentUser = AuthenticationChecker.checkAuthentication();
    questionService.addReview(new QuestionReviewCreateDTO(currentUser.id(), questionReviewCreateDTO.questionId(), questionReviewCreateDTO.content(), questionReviewCreateDTO.lineStart(), questionReviewCreateDTO.lineEnd(), questionReviewCreateDTO.tags() == null ? List.of() : questionReviewCreateDTO.tags()));
    return "redirect:../../light/questions/" + questionReviewCreateDTO.questionId();
  }

  @GetMapping("/answer/{reviewId}")
  @RequireUser
  public String answerReviewPage(@ModelAttribute("body") ReviewOnReviewBodyDTO reviewOnReviewBodyDTO, @PathVariable("reviewId") long questionId, Model model) {
    model.addAttribute("request", new ReviewOnReviewBodyDTO(questionId, reviewOnReviewBodyDTO.content(), reviewOnReviewBodyDTO.tagList()));
    return "pages/reviews/createResponse";
  }

  @PostMapping("/answer")
  @RequireUser
  public String answerReview(@Valid @ModelAttribute("request") ReviewOnReviewBodyDTO reviewOnReviewBodyDTO) {
    var currentUser = AuthenticationChecker.checkAuthentication();
    reviewService.addReview(new ReviewOnReviewDTO(currentUser.id(), reviewOnReviewBodyDTO.reviewId(), reviewOnReviewBodyDTO.content(), reviewOnReviewBodyDTO.tagList()));
    return "redirect:../../light/reviews/" + reviewOnReviewBodyDTO.reviewId();
  }

  public record SubReviewAuthorContent(long id, String author, String content) {
  }
}
