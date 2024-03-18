package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.config.security.SecurityContext;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionReviewCreateDTO;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.Tag;
import fr.pentagon.ugeoverflow.model.User;
import fr.pentagon.ugeoverflow.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public List<String> tagByCurrentUser() {
        var currentUser = SecurityContext.checkAuthentication();
        return tagRepository.findByUser(currentUser.id())
                .stream()
                .map(Tag::getName)
                .toList();
    }

    void addTag(User user, Review review, List<String> strings) {
        strings.forEach(tag -> {
            var existingTagOptional = tagRepository.findTagByName(tag);
            if (existingTagOptional.isEmpty()) {
                var newTag = new Tag(tag);
                tagRepository.save(newTag);
                user.addTag(newTag);
                review.addTag(newTag);
            } else {
                var existingTag = existingTagOptional.get();
                user.addTag(existingTag);
                review.addTag(existingTag);
            }
        });
    }
}
