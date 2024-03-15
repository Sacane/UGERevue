package fr.pentagon.ugeoverflow.algorithm;

import fr.pentagon.ugeoverflow.model.Question;

import java.util.List;
import java.util.Locale;

public interface QuestionSorterStrategy {
    QuestionSorterStrategy WITH_AUTHOR = (username, origins) -> origins.stream()
            .filter(question -> question.getAuthor().getUsername().toLowerCase(Locale.ROOT).contains(username.toLowerCase()))
            .toList();
    List<Question> getQuestions(String token, List<Question> origins);
}
