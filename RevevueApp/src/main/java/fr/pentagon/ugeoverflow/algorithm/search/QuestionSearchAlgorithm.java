package fr.pentagon.ugeoverflow.algorithm.search;

import fr.pentagon.ugeoverflow.model.Question;

@FunctionalInterface
public interface QuestionSearchAlgorithm {
    int MINIMAL_SCORE = 20;
    QuestionSearchAlgorithm IDENTITY = (t, q) -> 0;
    QuestionSearchAlgorithm ALL_ACCEPTED = (t, q) -> MINIMAL_SCORE;
    int apply(String token, Question question);
}
