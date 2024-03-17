package fr.pentagon.ugeoverflow.controllers.mvc;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/light")
public class MvcHomeController {
  @GetMapping("/home")
  public String homePage() {
    return "pages/home";
  }

  @GetMapping("")
  public String redirectToHome() {
    return "redirect:/light/home";
  }
}
