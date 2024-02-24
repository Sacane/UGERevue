package fr.pentagon.ugeoverflow.controllers.dtos.requests;

import java.util.Arrays;
import java.util.Objects;

public record NewQuestionDTO(String title, String description, byte[] javaFile, byte[] testFile) {

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
