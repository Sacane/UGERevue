package fr.pentagon.ugeoverflow.controllers.mvc;

import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/light/profile/")
public class MvcUserController {
    private final UserService userService;
    private final QuestionService questionService;

    public MvcUserController(UserService userService, QuestionService questionService) {
        this.userService = userService;
        this.questionService = questionService;
    }
    @GetMapping()
    public String profilePage(Model model){
        var userResponse = SecurityContext.authentication();
        if(userResponse.isEmpty()){
            return "redirect:/light/forbidden";
        }
        var user = userService.findById(userResponse.get().id());
        model.addAttribute("username", user.username());
        model.addAttribute("email", user.email());
        model.addAttribute("login", user.login());
        model.addAttribute("role", user.role().displayName());

        var questions = questionService.getQuestions();
        model.addAttribute("questions", questions);
        return "pages/users/profile";
    }
}
