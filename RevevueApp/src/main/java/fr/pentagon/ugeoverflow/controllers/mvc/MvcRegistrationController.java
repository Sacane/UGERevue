package fr.pentagon.ugeoverflow.controllers.mvc;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.UserRegisterDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/light")
public class MvcRegistrationController {
  private final UserService userService;

  public MvcRegistrationController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/register")
  public String registrationPage(@ModelAttribute("userRegisterDTO") UserRegisterDTO userRegisterDTO) {
    return "pages/register";
  }

  @PostMapping("/register")
  public String register(@Valid @ModelAttribute("userRegisterDTO") UserRegisterDTO userRegisterDTO,
                         BindingResult bindingResult, Model model) {
    if (bindingResult.hasErrors()) {
      return "pages/register";
    }
    try {
      userService.register(userRegisterDTO);
    } catch (HttpException e) {
      if (e.getStatusCode() == 400) {
        model.addAttribute("accountExists", true);
        return "pages/register";
      }
      throw e;
    }
    return "pages/register-success";
  }
}
