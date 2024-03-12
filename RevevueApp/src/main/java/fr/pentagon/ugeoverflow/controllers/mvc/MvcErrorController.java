package fr.pentagon.ugeoverflow.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/light")
public class MvcErrorController {

    @GetMapping("/forbidden")
    public String forbidden(){
        return "error/401";
    }
}
