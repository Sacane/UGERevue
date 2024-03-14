package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Objects;

public record NewQuestionDTO(
        @NotNull
        @NotBlank(message = "Un titre ne peut être vide.")
        String title,
        @NotNull
        @NotBlank(message = "Une description ne peut être vide")
        @Size(min = 8, message = "Veuillez détailler plus en détail votre problème.")
        String description,
        @NotNull
        byte[] javaFile,
        byte[] testFile,
        @NotNull
        @NotBlank(message = "le nom du fichier ne peut être vide.")
        String javaFilename,
        String testFilename
) {

    public NewQuestionDTO {
        Objects.requireNonNull(title);
        Objects.requireNonNull(javaFile);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NewQuestionDTO other && other.title.equals(title)
                && Arrays.equals(other.javaFile, javaFile) && Arrays.equals(other.testFile, testFile);
    }

    @Override
    public int hashCode() {
        return title.hashCode() ^ Arrays.hashCode(javaFile) ^ Arrays.hashCode(testFile);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "{" +
                "title='" + title + '\'' +
                ", javaFile=" + Arrays.toString(javaFile) +
                ", testFile=" + Arrays.toString(testFile) +
                '}';
    }

}
