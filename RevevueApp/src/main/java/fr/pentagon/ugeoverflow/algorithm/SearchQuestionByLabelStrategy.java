package fr.pentagon.ugeoverflow.algorithm;

import fr.pentagon.ugeoverflow.model.Question;

import java.util.*;

public class SearchQuestionByLabelStrategy implements QuestionSorterStrategy {
    private final String label;
    private static final int TITLE_POINT = 4;
    private static final int DESCRIPTION_POINT = 2;
    public SearchQuestionByLabelStrategy(String label){
        this.label = label;
    }

    @Override
    public List<Question> getQuestions(List<Question> origins) {
        var result = new TreeMap<Integer, Question>(Collections.reverseOrder());
        var tokens = Arrays.stream(this.label.split(" "))
                .map(String::toLowerCase)
                .toArray(String[]::new);
        for (Question question : origins) {
            int scoreByQuestion = getScoreByQuestion(question, tokens);
            result.put(scoreByQuestion, question);
        }
        return new ArrayList<>(result.values());
    }
    int getScoreByQuestion(Question question, String[] tokens) {
        int score = 0;
        String description = question.getDescription().toLowerCase();
        String title = question.getTitle().toLowerCase();

        for (String t : tokens) {
            var token = t.toLowerCase();
            int tokenScore = TITLE_POINT * token.length();
            if (title.contains(token)) {
                score += tokenScore;
                if (countOccurrences(title, token) == 1) {
                    score += TITLE_POINT * 2;
                }
            }
        }

        // Calcul du score pour la description
        for (String t : tokens) {
            var token = t.toLowerCase();
            int tokenScore = DESCRIPTION_POINT * token.length();
            score += tokenScore * countOccurrences(description, token);
        }
        return score;
    }

    // Méthode utilitaire pour compter le nombre d'occurrences d'une sous-chaîne dans une chaîne
    private int countOccurrences(String str, String subStr) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(subStr, idx)) != -1) {
            count++;
            idx += subStr.length();
        }
        return count;
    }
}
