package fr.pentagon.ugeoverflow.algorithm.search;

import fr.pentagon.ugeoverflow.model.Question;

@FunctionalInterface
public interface SearchAlgorithm {
    SearchAlgorithm IDENTITY = (t, q) -> 0;
    int apply(String token, Question question);
}
