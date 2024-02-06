package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.ReviewCreateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseEntity<ReviewDTO> create(ReviewCreateDTO reviewCreateDTO) {
        var userFind = userRepository.findById(reviewCreateDTO.userId());

        if (userFind.isEmpty()) {
            throw HttpException.notFound("User not exist");
        }
        var user = userFind.get();
        var review = reviewRepository.save(new Review(reviewCreateDTO.title(), reviewCreateDTO.javaFile(), reviewCreateDTO.testFile(), true, "OPEN", user, new Date()));

        return ResponseEntity.ok(new ReviewDTO(review.getId(), review.getTitle(), new String(review.getJavaFile(), StandardCharsets.UTF_8), new String(review.getTestFile() == null ? new byte[0] : review.getTestFile(), StandardCharsets.UTF_8), user.getId(), review.getCreatedAt()));
    }
}
