package fr.pentagon.ugeoverflow.controllers.mvc;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.thymleaf.NewQuestionThymeleafDTO;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.service.ReviewMarkdownService;
import fr.pentagon.ugeoverflow.utils.MarkdownRenderer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.logging.Logger;

@Controller
@RequestMapping("/light/questions/")
public class MvcQuestionController {
  private final QuestionService questionService;
  private final ReviewMarkdownService reviewService;
  private final MarkdownRenderer markdownRenderer;
  private final Logger logger = Logger.getLogger(MvcQuestionController.class.getName());

  public MvcQuestionController(QuestionService questionService, ReviewMarkdownService reviewService, MarkdownRenderer markdownRenderer) {
    this.questionService = questionService;
    this.reviewService = reviewService;
    this.markdownRenderer = markdownRenderer;
  }

  @GetMapping("/{questionId}")
  public String detail(@PathVariable("questionId") @Positive long questionId, Model model) {
    var question = questionService.findById(questionId);
    var reviews = reviewService.findReviewsByQuestionId(questionId);
    model.addAttribute("question", question.withAnotherContent(markdownRenderer.markdownToHtml(question.questionContent())));
    model.addAttribute("reviews", reviews);
    return "pages/questions/detail";
  }

  @PostMapping("/upvote/{questionId}")
  @RequireUser
  public String upvoteQuestion(@PathVariable("questionId") @Positive long questionId) {
    var user = SecurityContext.checkAuthentication();
    questionService.vote(user.id(), questionId, true);
    return "pages/return";
  }

  @PostMapping("/downvote/{questionId}")
  @RequireUser
  public String downvoteQuestion(@PathVariable("questionId") @Positive long questionId) {
    var user = SecurityContext.checkAuthentication();
    questionService.vote(user.id(), questionId, false);
    return "pages/return";
  }

  @GetMapping
  public String all(Model model) {
    var questions = questionService.getQuestions();
    model.addAttribute("questions", questions);
    return "pages/questions/all";
  }

  @GetMapping("/ask")
  public String askPage(@ModelAttribute("newQuestion") NewQuestionThymeleafDTO newQuestionDTO) {
    if (SecurityContext.authentication().isEmpty()) {
      throw HttpException.unauthorized("Non connecté");
    }
    return "pages/questions/ask";
  }

  @PostMapping("/ask")
  @RequireUser
  public String ask(@Valid @ModelAttribute("newQuestion") NewQuestionThymeleafDTO newQuestionDTO,
                    BindingResult bindingResult, Authentication authentication
  ) {
    logger.info("Post question : " + newQuestionDTO.javaFile().getOriginalFilename());
    if (authentication == null) return "redirect:light/login";
    if (bindingResult.hasErrors()) {
      return "pages/questions/ask";
    }
    try {
      questionService.create(newQuestionDTO.toNewQuestionDTO(), SecurityContext.checkAuthentication().id());
    } catch (IOException e) {
      logger.severe(e.getMessage());
      throw HttpException.badRequest("Le fichier n'a pas été correctement ouvert");
    }
    return "redirect:";
  }


}
