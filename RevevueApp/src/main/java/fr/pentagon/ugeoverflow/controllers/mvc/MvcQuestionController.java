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
        // Charger les détails de la question en utilisant l'ID de la question
        var question = questionService.findById(questionId);
        // Ajouter la question au modèle pour l'afficher dans la vue
        model.addAttribute("question", question);
        // Retourner la vue pour afficher les détails de la question
        return "/pages/questions/detail";
    }
}
