package fr.pentagon.ugeoverflow.service.mapper;

import fr.pentagon.ugeoverflow.controllers.dtos.responses.DetailReviewResponseDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewQuestionResponseDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.ReviewResponseDTO;
import fr.pentagon.ugeoverflow.model.Review;
import fr.pentagon.ugeoverflow.model.Tag;
import fr.pentagon.ugeoverflow.repository.ReviewVoteRepository;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewMapper {
    private final ReviewVoteRepository reviewVoteRepository;

    public ReviewMapper(ReviewVoteRepository reviewVoteRepository) {
        this.reviewVoteRepository = reviewVoteRepository;
    }

    public ReviewResponseDTO  entityToReviewResponseDTO(Review review) {
        return new ReviewResponseDTO(
                review.getAuthor().getUsername(),
                review.getContent(),
                reviewVoteRepository.findUpvoteNumberByReviewId(review.getId()),
                reviewVoteRepository.findDownvoteNumberByReviewId(review.getId()),
                review.getCreatedAt());
    }

    public ReviewQuestionResponseDTO entityToReviewQuestionResponseDTO(
            Review review,
            String authorName
    ){
        return new ReviewQuestionResponseDTO(
                review.getId(),
                authorName,
                review.getCreatedAt(),
                review.getContent(),
                null,
                reviewVoteRepository.findUpvoteNumberByReviewId(review.getId()),
                reviewVoteRepository.findDownvoteNumberByReviewId(review.getId()),
                List.of()
        );
    }
    public DetailReviewResponseDTO entityToDetailReviewResponseDTO(Review review, String citedCode, @Nullable Boolean doesUserVote, List<DetailReviewResponseDTO> list) {
        var start = review.getCodePart() != null ? review.getCodePart().getLineStart() : null;
        var end = review.getCodePart() != null ? review.getCodePart().getLineEnd() : null;
        System.out.println("start => " + start + " end => " + end);
        return new DetailReviewResponseDTO(
                review.getId(),
                review.getAuthor().getUsername(),
                review.getCreatedAt(),
                review.getContent(),
                citedCode,
                reviewVoteRepository.findUpvoteNumberByReviewId(review.getId()),
                reviewVoteRepository.findDownvoteNumberByReviewId(review.getId()),
                doesUserVote,
                list,
                review.getTagsList().stream().map(Tag::getName).toList(),
                start,
                end
        );
    }
}
