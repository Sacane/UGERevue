package fr.pentagon.ugeoverflow.service;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewUpdateDTO;
import fr.pentagon.ugeoverflow.model.embed.CodePart;
import fr.pentagon.ugeoverflow.repository.ReviewRepository;
import fr.pentagon.ugeoverflow.repository.TagRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ReviewServiceWithFailure {
    private final ReviewRepository reviewRepository;
    private final TagRepository tagRepository;

    public ReviewServiceWithFailure(ReviewRepository reviewRepository, TagRepository tagRepository) {
        this.reviewRepository = reviewRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public ReviewUpdateDTO updateById(long reviewId, ReviewUpdateDTO reviewUpdateDTO) {
        var review = reviewRepository.findByIdWithTags(reviewId).orElseThrow(() -> HttpException.notFound("This review does not exists"));
        var codePart = (reviewUpdateDTO.lineStart() == null || reviewUpdateDTO.lineEnd() == null) ? null : new CodePart(reviewUpdateDTO.lineStart(), reviewUpdateDTO.lineEnd());
        var tags = tagRepository.findAllByNames(reviewUpdateDTO.tags());
        review.getTagsList().clear();
        review.update(reviewUpdateDTO.content(), codePart, new HashSet<>(tags));
        return reviewUpdateDTO;
    }
}
