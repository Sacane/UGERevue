package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.Tag;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.TagRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TagService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final TagRepository tagRepository;

    public TagService(UserRepository userRepository, ReviewRepository reviewRepository, TagRepository tagRepository){
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public void registerTag(String tagName, long userId, long reviewId){
        var user = userRepository.findById(userId).orElseThrow(() -> HttpException.notFound("User not found"));
        var review = reviewRepository.findById(reviewId).orElseThrow(() -> HttpException.notFound("Review not found"));
        var existingTagOptional = tagRepository.findTagByName(tagName);
        if (existingTagOptional.isEmpty()) {
            var newTag = new Tag(tagName);
            tagRepository.save(newTag);
            user.addTag(newTag);
            review.addTag(newTag);
            newTag.addUser(user);
            newTag.addReview(review);
        } else {
            var existingTag = existingTagOptional.get();
            user.addTag(existingTag);
            review.addTag(existingTag);
            existingTag.addUser(user);
            existingTag.addReview(review);
        }
    }
}
