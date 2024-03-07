package fr.pentagon.ugeoverflow.algorithm.search;

@FunctionalInterface
public interface SearchAlgorithm {
    SearchAlgorithm IDENTITY = t -> 0;
    int apply(String token);
}
