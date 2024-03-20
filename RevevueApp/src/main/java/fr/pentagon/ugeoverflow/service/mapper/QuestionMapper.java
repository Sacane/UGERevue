package fr.pentagon.ugeoverflow.service.mapper;

import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDTO;
import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.repository.QuestionVoteRepository;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {
    private final QuestionVoteRepository questionVoteRepository;

    public QuestionMapper(QuestionVoteRepository questionVoteRepository) {
        this.questionVoteRepository = questionVoteRepository;
    }

    public QuestionDTO entityToQuestionDTO(Question question) {
        return new QuestionDTO(
                question.getId(),
                question.getTitle(),
                question.getDescription(),
                question.getAuthor().getUsername(),
                question.getCreatedAt().toString(),
                questionVoteRepository.findUpvoteNumberByQuestionId(question.getId()) - questionVoteRepository.findDownvoteNumberByQuestionId(question.getId()),
                question.getReviews().size()
        );
    }
}
