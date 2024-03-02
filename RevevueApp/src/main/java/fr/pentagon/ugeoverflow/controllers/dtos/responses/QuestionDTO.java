package fr.pentagon.ugeoverflow.controllers.dtos.responses;

public record QuestionDTO(
        long id,
        String title,
        String description,
        String userName,
        String date,
        long nbVotes,
        long nbAnswers
) {
}
