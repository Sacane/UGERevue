package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import java.util.Date;
import java.util.Objects;

public record QuestionDTO(long id, String title, String javaFile, String testFile, long authorID,
                          Date creationDate) {

    public QuestionDTO {
        Objects.requireNonNull(title);
        Objects.requireNonNull(javaFile);
        Objects.requireNonNull(testFile);
        Objects.requireNonNull(creationDate);
    }

}
