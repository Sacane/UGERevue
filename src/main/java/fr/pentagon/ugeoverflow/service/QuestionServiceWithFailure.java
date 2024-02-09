package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionUpdateDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceWithFailure {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public QuestionServiceWithFailure(QuestionRepository questionRepository, UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void update(QuestionUpdateDTO questionUpdateDTO) {
        var userFind = userRepository.findById(questionUpdateDTO.userId());
        if (userFind.isEmpty()) {
            throw HttpException.notFound("User not exist");
        }
        var questionFind = questionRepository.findById(questionUpdateDTO.questionId());
        if (questionFind.isEmpty()) {
            throw HttpException.notFound("Question not exist");
        }
        var user = userFind.get();
        var question = questionFind.get();

        if (!userRepository.containsQuestion(user.getId(), question)) {
            throw HttpException.unauthorized("Not your question");
        }

        if (questionUpdateDTO.title() != null) {
            question.setTitle(questionUpdateDTO.title());
        }
        if (questionUpdateDTO.description() != null) {
            question.setDescription(questionUpdateDTO.description());
        }
    }
}
