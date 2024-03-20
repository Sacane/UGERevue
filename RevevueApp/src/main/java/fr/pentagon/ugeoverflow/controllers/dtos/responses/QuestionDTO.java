package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import java.util.Date;

public record QuestionDTO(
        long id,
        String title,
        String description,
        String userName,
        Date date,
        long nbVotes,
        long nbAnswers
) {
}
