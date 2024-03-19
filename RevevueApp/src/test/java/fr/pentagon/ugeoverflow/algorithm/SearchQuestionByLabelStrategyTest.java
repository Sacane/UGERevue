package fr.pentagon.ugeoverflow.algorithm;

import fr.pentagon.ugeoverflow.algorithm.search.QuestionSearchAlgorithm;
import fr.pentagon.ugeoverflow.model.Question;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchQuestionByLabelStrategyTest {



    private final List<Question> provideQuestions = List.of(
            provideQuestionFromTitleAndDescription(
                    "Comment déclarer une List<Object> en Java ?",
                    "Je veux comprendre comment créer une classe abstraite en Java et à quoi cela sert-il dans la programmation orientée objet."
            ),
            provideQuestionFromTitleAndDescription(
                    "Quelle est la différence entre les classes abstraites et les interfaces en Java ?",
                    "J'ai entendu parler de classes abstraites et d'interfaces en Java, mais je ne suis pas sûr de comprendre les différences entre les deux. Quelles sont-elles et quand devrais-je utiliser l'une plutôt que l'autre ?"
            ),
            provideQuestionFromTitleAndDescription(
                    "Comment implémenter l'héritage en Java ?",
                    "J'ai une hiérarchie de classes en Java et je veux comprendre comment utiliser l'héritage pour réutiliser du code et organiser ma structure de programme de manière efficace."
            ),
            provideQuestionFromTitleAndDescription(
                    "Qu'est-ce qu'une exception en Java et comment la gérer ?",
                    "J'ai vu des exceptions en Java mais je ne sais pas exactement ce qu'elles sont ni comment les traiter correctement dans mes programmes. Pouvez-vous m'expliquer les bases de la gestion des exceptions en Java ?"
            ),
            provideQuestionFromTitleAndDescription(
                    "Comment lire et écrire des fichiers en Java ?",
                    "J'ai besoin de manipuler des fichiers dans mon programme Java, mais je ne sais pas comment ouvrir, lire et écrire des données dans des fichiers. Pouvez-vous me montrer comment le faire efficacement en Java ?"
            ),
            provideQuestionFromTitleAndDescription(
                    "Qu'est-ce qu'une collection en Java et quelles sont les différentes types de collections disponibles ?",
                    "Je veux comprendre ce qu'est une collection en Java et comment je peux utiliser différentes implémentations de collections pour stocker et manipuler des données dans mes programmes."
            ),
            provideQuestionFromTitleAndDescription(
                    "Comment travailler avec les threads en Java pour réaliser du multi-threading ?",
                    "Je suis intéressé par le multi-threading en Java mais je ne sais pas par où commencer. Pouvez-vous me montrer comment créer et gérer des threads en Java pour exécuter des tâches concurrentes dans mon application ?"
            ),
            provideQuestionFromTitleAndDescription(
                    "Qu'est-ce que la programmation orientée objet en Java et pourquoi est-ce important ?",
                    "Je suis nouveau en Java et j'entends beaucoup parler de programmation orientée objet. Pouvez-vous m'expliquer les principes de base de la POO en Java et pourquoi c'est une approche importante pour le développement logiciel ?"
            ),
            provideQuestionFromTitleAndDescription(
                    "Comment utiliser les expressions lambda en Java 8 pour simplifier mon code ?",
                    "J'ai entendu dire que Java 8 introduit des expressions lambda, mais je ne suis pas sûr de leur utilité ni de la manière de les utiliser dans mon code. Pouvez-vous me montrer quelques exemples pratiques d'utilisation des expressions lambda en Java ?"
            ),
            provideQuestionFromTitleAndDescription(
                    "Qu'est-ce que JDBC et comment l'utiliser pour interagir avec une base de données en Java ?",
                    "Je veux apprendre à accéder et à manipuler des données dans une base de données à partir de mon programme Java. Pouvez-vous m'expliquer ce qu'est JDBC et comment je peux l'utiliser pour établir une connexion à une base de données et exécuter des requêtes SQL ?"
            )
    );


    private Question provideQuestionFromTitleAndDescription(
            String title, String description
    ){
        return new Question(title, description, new byte[]{}, null, "", true, new Date());
    }

    @Test
    void scoreResultTest() {
        var strategy = new SearchQuestionByLabelStrategy();
        var question =  provideQuestionFromTitleAndDescription(
                "Comment déclarer une classe abstraite en Java ?",
                "Je veux comprendre comment créer une classe abstraite en Java et à quoi cela sert-il dans la programmation orientée objet."
        );
        var question2 = provideQuestionFromTitleAndDescription(
                "Comment déclarer une classe abstraite en Java ?",
                "J'ai vu des exceptions en Java mais je ne sais pas exactement ce qu'elles sont ni comment les traiter correctement dans mes programmes. Pouvez-vous m'expliquer les bases de la gestion des exceptions en Java ?"
        );
        String[] prompt = {"Comment", "déclarer", "une", "classe", "abstraite", "en", "Java"};
        var questionScore = strategy.getScoreByQuestion(question, prompt, QuestionSearchAlgorithm.IDENTITY);
        var questionScore2 = strategy.getScoreByQuestion(question2, prompt, QuestionSearchAlgorithm.IDENTITY);
        assertTrue(questionScore > questionScore2);
    }

    @Test
    void algorithmTest(){
        var strategy = new SearchQuestionByLabelStrategy();
        var result = strategy.getQuestions("Comment lire et écrire des fichiers en Java ?", provideQuestions);
        assertEquals("Comment lire et écrire des fichiers en Java ?", result.getFirst().getTitle());
    }

    @Test
    void noQuestionResultTest() {
        var strategy = new SearchQuestionByLabelStrategy();
        assertEquals(0, strategy.getQuestions("où rechercher mon beurre demain ?", provideQuestions).size());
    }
}
