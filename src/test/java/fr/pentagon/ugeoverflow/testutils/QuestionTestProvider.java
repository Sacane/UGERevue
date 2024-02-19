package fr.pentagon.ugeoverflow.testutils;

import fr.pentagon.ugeoverflow.repository.QuestionRepository;

public class QuestionTestProvider {
    private final QuestionRepository questionRepository;

    public QuestionTestProvider(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }
}
