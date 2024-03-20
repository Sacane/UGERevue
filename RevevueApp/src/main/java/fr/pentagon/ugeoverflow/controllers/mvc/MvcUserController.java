package fr.pentagon.ugeoverflow.controllers.mvc;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.AuthenticationChecker;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserFollowInfoDTO;
import fr.pentagon.ugeoverflow.service.QuestionService;
import fr.pentagon.ugeoverflow.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/light")
public class MvcUserController {
    private final UserService userService;
    private final QuestionService questionService;

    public MvcUserController(UserService userService, QuestionService questionService) {
        this.userService = userService;
        this.questionService = questionService;
    }

    @GetMapping("/profile")
    @RequireUser
    public String profilePage(Model model, Principal principal) {
        var userResponse = AuthenticationChecker.authentication();
        if (userResponse.isEmpty()) {
            throw HttpException.unauthorized("Non connecté");
        }
        var user = userService.findById(userResponse.get().id());
        model.addAttribute("username", user.username());
        model.addAttribute("email", user.email());
        model.addAttribute("login", user.login());
        model.addAttribute("role", user.role().displayName());

        var questions = questionService.getQuestionsFromCurrentUser(principal.getName());
        model.addAttribute("questions", questions);
        var followed = userService.getUserFollowings(userResponse.get().id());
        model.addAttribute("follows", followed);
        return "pages/users/profile";
    }

    @GetMapping("/social")
    public String socialPage(Model model) {
        var userResponse = AuthenticationChecker.authentication();
        List<UserFollowInfoDTO> peoples;
        if (userResponse.isPresent()) {
            var id = userResponse.get().id();
            peoples = userService.userRegisteredList(id);
        } else {
            peoples = userService.userRegisteredList();
        }
        model.addAttribute("peoples", peoples);
        return "pages/users/social";
    }


}
