package fr.pentagon.ugeoverflow.algorithm;

import fr.pentagon.ugeoverflow.algorithm.search.CommonJavaSearchAlgorithm;
import fr.pentagon.ugeoverflow.algorithm.search.OnDescriptionContainsAlgorithm;
import fr.pentagon.ugeoverflow.algorithm.search.OnTitleContainsAlgorithm;
import fr.pentagon.ugeoverflow.algorithm.search.SearchAlgorithm;
import fr.pentagon.ugeoverflow.model.Question;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchQuestionByLabelStrategy implements QuestionSorterStrategy {
    private final String label;
    public static final int TITLE_POINT = 4;
    public static final int DESCRIPTION_POINT = 2;
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
            System.out.println(scoreByQuestion + " -> " + question.getTitle() + " || "+ question.getDescription());
            if(scoreByQuestion < 20) {
                continue; // question is ignored on zero-score
            }
            result.put(scoreByQuestion, question);
        }
        return new ArrayList<>(result.values());
    }

    int getScoreByQuestion(Question question, String[] tokens) {
        int score = 0;
        String description = question.getDescription().toLowerCase();
        String title = question.getTitle().toLowerCase();
        SearchAlgorithm searchAlgorithm = new OnTitleContainsAlgorithm(new OnDescriptionContainsAlgorithm(new CommonJavaSearchAlgorithm(SearchAlgorithm.IDENTITY), description), title);
        for (String t : tokens) {
            var token = t.toLowerCase();
            score += searchAlgorithm.apply(token);
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
