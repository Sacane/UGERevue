package fr.pentagon.ugeoverflow.service;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import fr.pentagon.ugeoverflow.controllers.dtos.responses.QuestionDetailDTO;
import fr.pentagon.ugeoverflow.exception.HttpException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@Profile("test")
public final class FakeQuestionService implements QuestionServiceAdapter {

    private static final Set<Long> KNOWN_USERS = new HashSet<>(Set.of(118218L, 3630L, 17L));

    private static final List<QuestionDetailDTO> INITIAL_QUESTIONS = List.of(
            new QuestionDetailDTO(
                    0L,
                    "How to concat string in for statement",
                    "StringBuilder",
                    "",
                    118218L,
                    Date.from(Instant.now())
            ),
            new QuestionDetailDTO(
                    7L,
                    "Should I use C++",
                    "Maybe not",
                    "@Test",
                    3630L,
                    Date.from(Instant.parse("2007-12-03T10:15:30.00Z"))
            ),
            new QuestionDetailDTO(
                    14L,
                    "Should I use C++",
                    "Maybe not",
                    "",
                    17L,
                    Date.from(Instant.now())
            )
    );

    private final Map<Long, QuestionDetailDTO> questions;

    public FakeQuestionService(){
        var questions = new HashMap<Long, QuestionDetailDTO>();
        INITIAL_QUESTIONS.forEach(q -> {
            questions.put(q.id(), q);
        });
        this.questions = questions;
    }

    @Override
    public List<QuestionDetailDTO> allQuestions() {
        return questions.values().stream().toList();
    }

    @Override
    public QuestionDetailDTO registerQuestion(NewQuestionDTO newQuestionDTO, long authorId) throws HttpException {
        Objects.requireNonNull(newQuestionDTO);
        var random = new Random();
        long questionId = random.nextLong();
        while (questions.containsKey(questionId)){
            questionId = random.nextLong();
        }
        if(!KNOWN_USERS.contains(authorId)) throw new HttpException(404, "User not found");
        var questionDTO = new QuestionDetailDTO(questionId,
                newQuestionDTO.title(),
                new String(newQuestionDTO.javaFile()),
                new String(newQuestionDTO.testFile()),
                authorId, Date.from(Instant.now())
        );
        questions.put(questionDTO.id(), questionDTO);
        return questionDTO;
    }

    @Override
    public boolean removeQuestion(long questionId) {
        if(questions.containsKey(questionId)){
            questions.remove(questionId);
            return true;
        }
        return false;
    }

    @Override
    public Optional<QuestionDetailDTO> question(long questionId) {
        if(questions.containsKey(questionId)){
            return Optional.of(questions.get(questionId));
        }
        return Optional.empty();
    }

}