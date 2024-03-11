package fr.pentagon.ugeoverflow.service;

import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.JaccardDistance;

import java.util.*;

public final class QuestionMatcher {

    private static final double COSINUS_DISTANCE_ACCEPTATION = 0.8;
    private static final int LEVENSHTEIN_ACCEPTATION = 200;
    private static final double JACCARD_ACCEPTATION = 0.2;

    //Indique le nombre de transformations pour arriver d'un résultat à l'autre.
    //Plus le nombre est élevé plus, ils sont différents.
    private static int levenshteinImplementation(String s1, String s2) {
        var levenshteinObj = new LevenshteinDistance();
        return levenshteinObj.apply(s1,s2);
    }


    //Calcul de vecteur, plus le résultat est proche de 1 plus ça se ressemble
    private static double cosinusDistanceImplementation(String s1, String s2) {
        var cosinusObj = new CosineDistance();
        return cosinusObj.apply(s1, s2);
    }

    //Plus le résultat est petit plus la similarité est grande
    private static double jaccardDistanceImplementation(String s1, String s2) {
        var jaccardObj = new JaccardDistance();
        return jaccardObj.apply(s1, s2);
    }

    public static boolean isPertinentRecommendation(String question1, String question2){
        Objects.requireNonNull(question1);
        Objects.requireNonNull(question2);
        return cosinusDistanceImplementation(question1, question2) > COSINUS_DISTANCE_ACCEPTATION
                || jaccardDistanceImplementation(question1, question2) < JACCARD_ACCEPTATION;
    }

    public static void main(String[] args) {
        String question1 = "Quelle est votre couleur préférée ?";
        String question2 = "Quelle couleur aimez-vous le plus parmis toute celle présente dans l'arc en ciel mais je ne comprend pas trop la différence entre les deux ?";
        String question3 = "Quelle est votre couleur préféré ?";

        var tokens = new ArrayList<>(Arrays.asList(question1.split("\\s+")));
        tokens.forEach(System.out::println);

        System.out.println(isPertinentRecommendation(question1, question3));
    }
}
