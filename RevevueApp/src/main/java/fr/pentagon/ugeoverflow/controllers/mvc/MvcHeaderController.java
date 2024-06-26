package fr.pentagon.ugeoverflow.controllers.mvc;

import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.service.QuestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/light")
public class MvcHeaderController {
  private final QuestionService questionService;

  public MvcHeaderController(QuestionService questionService) {
    this.questionService = questionService;
  }

  @PostMapping("/search")
  public String search(@Valid @ModelAttribute("parameter") LabelUserParameter labelUserParameter, Model model) {
    List<QuestionDTO> questions = questionService.getQuestions(labelUserParameter.label, labelUserParameter.user);
    model.addAttribute("questions", questions);
    return "pages/questions/search";
  }

  @GetMapping("/search")
  public String searchPage() {
    return "pages/questions/search";
  }

  public record LabelUserParameter(@NotNull String label, @NotNull String user) {
  }
}

