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
        var userQuestion = QuestionService.findQuestionFromId(userRepository, questionUpdateDTO.userId(), questionRepository, questionUpdateDTO.questionId());
        var user = userQuestion.user();
        var question = userQuestion.question();

        if (!userRepository.containsQuestion(user.getId(), question)) {
            throw HttpException.unauthorized("Not your question");
        }

        if (questionUpdateDTO.title() != null) {
            question.setTitle(questionUpdateDTO.title());
        }
        if (questionUpdateDTO.description() != null) {
            question.setDescription(questionUpdateDTO.description());
        }
        if (questionUpdateDTO.file() != null) {
            question.setFile(questionUpdateDTO.file());
        }
        if (questionUpdateDTO.testFile() != null) {
            question.setTestFile(questionUpdateDTO.testFile());
            question.setTestResult("TEST RESULT"); //TODO test
        }
    }
}
