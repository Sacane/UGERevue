package fr.pentagon.ugeoverflow.algorithm;

import fr.pentagon.ugeoverflow.algorithm.search.*;
import fr.pentagon.ugeoverflow.model.Question;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchQuestionByLabelStrategy implements QuestionSorterStrategy {
    public static final int DESCRIPTION_POINT = 2;

    @Override
    public List<Question> getQuestions(String label, List<Question> origins) {
        var result = new TreeMap<Integer, List<Question>>(Collections.reverseOrder());
        var tokens = Arrays.stream(label.split(" "))
                .map(String::toLowerCase)
                .toArray(String[]::new);
        QuestionSearchAlgorithm minimal = (label.isEmpty()) ? QuestionSearchAlgorithm.ALL_ACCEPTED : QuestionSearchAlgorithm.IDENTITY;
        for (Question question : origins) {
            int scoreByQuestion = getScoreByQuestion(question, tokens, minimal);
            if(scoreByQuestion < QuestionSearchAlgorithm.MINIMAL_SCORE) {
                continue;
            }
            result.computeIfAbsent(scoreByQuestion, k -> new ArrayList<>()).add(question);
        }
        return result.values().stream()
                .flatMap(List::stream)
                .toList();
    }

    int getScoreByQuestion(Question question, String[] tokens, QuestionSearchAlgorithm minimalAlgorithm) {
        int score = 0;
        String description = question.getDescription().toLowerCase();
        String title = question.getTitle().toLowerCase();
        QuestionSearchAlgorithm questionSearchAlgorithm = new OnTitleContainsAlgorithmQuestion(new OnDescriptionContainsAlgorithmQuestion(new CommonJavaQuestionSearchAlgorithm(minimalAlgorithm), description), title);
        for (String t : tokens) {
            var token = t.toLowerCase();
            score += questionSearchAlgorithm.apply(token, question);
        }
        return score;
    }

    public static int countOccurrences(String str, String token) {
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(token) + "\\b");
        Matcher matcher = pattern.matcher(str);

        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
