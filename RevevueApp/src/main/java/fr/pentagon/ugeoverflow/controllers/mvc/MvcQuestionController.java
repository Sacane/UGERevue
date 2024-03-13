package fr.pentagon.ugeoverflow.controllers.mvc;

import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.thymleaf.NewQuestionThymeleafDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.service.QuestionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.logging.Logger;

@Controller
@RequestMapping("/light/questions")
public class MvcQuestionController {
    private final QuestionService questionService;
    private final Logger logger = Logger.getLogger(MvcQuestionController.class.getName());
    public MvcQuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/{questionId}")
    public String detail(@PathVariable("questionId") long questionId, Model model) {
        var question = questionService.findById(questionId);
        model.addAttribute("question", question);
        return "/pages/questions/detail";
    }

    @GetMapping("/")
    public String all(Model model) {
        var questions = questionService.getQuestions();
        model.addAttribute("questions", questions);
        return "/pages/questions/all";
    }

    @GetMapping("/ask")
    public String askPage(@ModelAttribute("newQuestion") NewQuestionThymeleafDTO newQuestionDTO) {
        if(SecurityContext.authentication().isEmpty()){
            return "redirect:/light/forbidden";
        }
        return "/pages/questions/ask";
    }

    @PostMapping("/ask")
    public String ask(@Valid @ModelAttribute("newQuestion") NewQuestionThymeleafDTO newQuestionDTO,
                      BindingResult bindingResult, Authentication authentication
    ){
        logger.info("Post question : " + newQuestionDTO.javaFile().getOriginalFilename());
        if(authentication == null) return "redirect:light/login";
        if(bindingResult.hasErrors()) {
            return "pages/questions/ask";
        }
        try {
            questionService.create(newQuestionDTO.toNewQuestionDTO(), SecurityContext.checkAuthentication().id());
        }catch (IOException e){
            logger.severe(e.getMessage());
            throw HttpException.badRequest("Le fichier n'a pas été correctement ouvert");
        }
        return "redirect:";
    }
}
