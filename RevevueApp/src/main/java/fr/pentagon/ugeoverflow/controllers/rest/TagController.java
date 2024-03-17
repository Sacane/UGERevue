package fr.pentagon.ugeoverflow.controllers.rest;

import fr.pentagon.ugeoverflow.config.authorization.RequireUser;
import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.service.TagService;
import fr.pentagon.ugeoverflow.utils.Routes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TagController {
    public record TagWrapperDTO(String tag){}
    private final TagService tagService;
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(Routes.Tag.ROOT)
    @RequireUser
    public ResponseEntity<List<TagWrapperDTO>> getAllOfUser(){
        var currentUser = SecurityContext.checkAuthentication();
        return ResponseEntity.ok(tagService.tagByCurrentUser(currentUser.id()).stream().map(TagWrapperDTO::new).toList());
    }
}
