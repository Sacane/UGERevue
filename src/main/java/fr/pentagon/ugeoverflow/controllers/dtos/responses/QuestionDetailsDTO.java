package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import java.util.List;

public record QuestionDetailsDTO(
    long id,
    String author,
    String creationDate,
    List<String> tags,
    String title,
    String questionContent,
    String classContent,
    String testClassContent,
    String testResults,
    long voteCount,
    long commentCount
) {
}
