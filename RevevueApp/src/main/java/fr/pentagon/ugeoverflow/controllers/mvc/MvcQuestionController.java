package fr.pentagon.ugeoverflow.controllers.mvc;

import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/light/questions")
public class MvcQuestionController {
    private final QuestionService questionService;
    public MvcQuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/{questionId}")
    public String detail(@PathVariable("questionId") long questionId, Model model) {
        var question = questionService.findById(questionId);
        model.addAttribute("question", question);
        return "/pages/questions/detail";
    }

    @GetMapping
    public String all(Model model) {
        var questions = questionService.getQuestions();
        model.addAttribute("questions", questions);
        return "/pages/questions/all";
    }
}
