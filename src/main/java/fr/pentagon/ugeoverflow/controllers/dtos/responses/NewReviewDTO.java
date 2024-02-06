package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import java.util.Arrays;
import java.util.Objects;

public record NewReviewDTO(String title, byte[] javaFile, byte[] testFile, long authorID) {

    public NewReviewDTO {
        Objects.requireNonNull(title);
        Objects.requireNonNull(javaFile);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NewReviewDTO other && other.title.equals(title)
                && Arrays.equals(other.javaFile, javaFile) && Arrays.equals(other.testFile, testFile)
                && other.authorID == authorID;
    }

    @Override
    public int hashCode() {
        return title.hashCode() ^ Arrays.hashCode(javaFile) ^ Arrays.hashCode(testFile)
                ^ Long.hashCode(authorID);
    }

    @Override
    public String toString() {
        return "NewReviewDTO{" +
                "title='" + title + '\'' +
                ", javaFile=" + Arrays.toString(javaFile) +
                ", testFile=" + Arrays.toString(testFile) +
                ", authorID=" + authorID +
                '}';
    }

}
