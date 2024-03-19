package fr.pentagon.ugeoverflow.algorithm;

import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.JaccardDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Objects;

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
    //le rapport entre le cardinal (la taille) de l'intersection des ensembles considérés et le cardinal de l'union des ensembles
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
}
