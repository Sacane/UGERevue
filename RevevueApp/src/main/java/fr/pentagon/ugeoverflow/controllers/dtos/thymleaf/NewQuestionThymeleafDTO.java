package fr.pentagon.ugeoverflow.controllers.dtos.thymleaf;

import fr.pentagon.ugeoverflow.controllers.dtos.requests.NewQuestionDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;


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