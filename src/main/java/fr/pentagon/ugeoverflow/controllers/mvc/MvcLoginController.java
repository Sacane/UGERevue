package fr.pentagon.ugeoverflow.controllers.mvc;

import fr.pentagon.ugeoverflow.controllers.LoginManager;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.CredentialsDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
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
  public String loginPage(Authentication authentication) {
    if (authentication != null) {
      return "redirect:/light";
    }
    return "pages/login";
  }

  @PostMapping("/login")
  public String login(@ModelAttribute("credentialsDTO") CredentialsDTO credentialsDTO, Authentication authentication,
                      HttpServletRequest request, HttpServletResponse response) {
    if (authentication != null) {
      return "redirect:/light";
    }
    var loginResponse = loginManager.login(credentialsDTO, request, response).orElse(null);
    if (loginResponse != null) {
      return "redirect:/light";
    }
    return "redirect:/light/login?failed=true";
  }

  @GetMapping("/logout")
  public String logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
    if (authentication == null) {
      return "redirect:/light";
    }
    loginManager.logout(request, response);
    return "redirect:/light";
  }
}
