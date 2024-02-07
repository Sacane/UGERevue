package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.QuestionCreateDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import fr.pentagon.ugeoverflow.model.Question;
import fr.pentagon.ugeoverflow.repository.QuestionRepository;
import fr.pentagon.ugeoverflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public QuestionService(QuestionRepository questionRepository, UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseEntity<Long> create(QuestionCreateDTO questionCreateDTO) {
        var userFind = userRepository.findById(questionCreateDTO.userId());

        if (userFind.isEmpty()) {
            throw HttpException.notFound("User not exist");
        }
        var user = userFind.get();
        var question = questionRepository.save(new Question(questionCreateDTO.title(), questionCreateDTO.descrition(), questionCreateDTO.file(), questionCreateDTO.testFile(), "TEST RESULT", true, new Date()));
        user.addQuestion(question);

        return ResponseEntity.ok(question.getId());
    }
}
