package fr.pentagon.ugeoverflow.controllers.dtos.requests;

public record SearchQuestionDTO(
        String questionLabelSearch,
        String usernameSearch
) {
}
