package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.model.Tag;
import fr.pentagon.ugeoverflow.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<String> tagByCurrentUser() {
        var currentUser = SecurityContext.checkAuthentication();
        return tagRepository.findByUser(currentUser.id())
                .stream()
                .map(Tag::getName)
                .toList();
    }
}
