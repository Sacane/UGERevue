package fr.pentagon.ugeoverflow.controllers.dtos.thymleaf;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;

public record NewQuestionThymeleafDTO(
        @NotNull
        @NotBlank
        String title,
        @NotNull
        @NotBlank
        String description,
        MultipartFile javaFile,
        MultipartFile testFile
) {

    public NewQuestionDTO toNewQuestionDTO() throws IOException {
        return new NewQuestionDTO(
                title,
                description,
                javaFile.getBytes(),
                testFile.getBytes(),
                javaFile.getOriginalFilename(),
                testFile.getOriginalFilename()
        );
    }
}