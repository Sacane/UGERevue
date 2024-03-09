package fr.pentagon.ugeoverflow.algorithm;

import fr.pentagon.ugeoverflow.model.Question;

import java.util.List;

public interface QuestionSorterStrategy {
    QuestionSorterStrategy DEFAULT = l -> l;
    List<Question> getQuestions(List<Question> origins);
}
