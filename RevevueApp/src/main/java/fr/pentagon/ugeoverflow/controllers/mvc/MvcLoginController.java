package fr.pentagon.ugeoverflow.controllers.mvc;

import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.CredentialsDTO;
import fr.pentagon.ugeoverflow.service.LoginManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/light")
public class MvcLoginController {
  private final LoginManager loginManager;

  public MvcLoginController(LoginManager loginManager) {
    this.loginManager = loginManager;
  }

  @GetMapping("/login")
  public String loginPage(@Valid @ModelAttribute("credentialsDTO") CredentialsDTO credentialsDTO, Authentication authentication) {
    if (authentication != null) {
      return "redirect:/light/home";
    }
    return "pages/login";
  }

  @PostMapping("/login")
  public String login(@Valid @ModelAttribute("credentialsDTO") CredentialsDTO credentialsDTO, BindingResult bindingResult,
                      Authentication authentication, HttpServletRequest request, HttpServletResponse response,
                      Model model) {
    if (authentication != null) {
      return "redirect:/light/home";
    }
    if (bindingResult.hasErrors()) {
      return "pages/login";
    }
    var loginResponse = loginManager.login(credentialsDTO, request, response).orElse(null);
    if (loginResponse != null) {
      return "redirect:/light/home";
    }
    model.addAttribute("loginFailed", true);
    return "pages/login";
  }

  @GetMapping("/logout")
  @RequireUser
  public String logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
    if (authentication == null) {
      return "redirect:/light/home";
    }
    loginManager.logout(request, response);
    return "redirect:/light/home";
  }
}
