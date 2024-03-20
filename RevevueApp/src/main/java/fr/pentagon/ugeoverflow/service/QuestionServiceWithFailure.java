package fr.pentagon.ugeoverflow.service;

import fr.pentagon.revevue.common.exception.HttpException;
import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionUpdateDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionUpdateResponseDTO;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class QuestionServiceWithFailure {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final TestServiceRunner testServiceRunner;

    public QuestionServiceWithFailure(QuestionRepository questionRepository, UserRepository userRepository, TestServiceRunner testServiceRunner) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.testServiceRunner = testServiceRunner;
    }

    @Transactional
    public QuestionUpdateResponseDTO update(long userId, long questionId, QuestionUpdateDTO questionUpdateDTO) {
        var userQuestion = QuestionService.findQuestionFromId(userRepository, userId, questionRepository, questionId);
        var user = userQuestion.user();
        var question = userQuestion.question();

        if (!userRepository.containsQuestion(user.getId(), question)) {
            throw HttpException.unauthorized("Not your question");
        }

        if (questionUpdateDTO.description() != null) {
            question.setDescription(questionUpdateDTO.description());
        }
        if (questionUpdateDTO.testFile() != null) {
            question.setTestFile(questionUpdateDTO.testFile());
            question.setTestFileName(questionUpdateDTO.testFilename());
            var result = testServiceRunner.sendTestAndGetFeedback(
                    question.getFileName(),
                    question.getTestFileName(),
                    question.getFile(),
                    question.getTestFile(),
                    userId
            );
            question.setTestResult(result);
        }

        return new QuestionUpdateResponseDTO(
                questionUpdateDTO.description(),
                question.getTestFile() != null ? new String(question.getTestFile(), StandardCharsets.UTF_8) : null,
                question.getTestFile() != null ? question.getTestResult() : null
        );
    }
}

