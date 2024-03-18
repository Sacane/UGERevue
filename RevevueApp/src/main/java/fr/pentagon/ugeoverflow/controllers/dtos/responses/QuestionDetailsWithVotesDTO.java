package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import jakarta.annotation.Nullable;

import java.util.List;

public record QuestionDetailsWithVotesDTO(
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
        long commentCount,
        long upvotes,
        long downvotes,
        @Nullable Boolean vote
) {
}
