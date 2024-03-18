package fr.pentagon.revevue.common.dto;

import java.util.Arrays;
import java.util.Objects;

public record TestBundle(long id, String testFileName, byte[] testFile, String dependencyFileName, byte[] dependencyFile) {

    public TestBundle {
        Objects.requireNonNull(testFileName);
        Objects.requireNonNull(testFile);
        Objects.requireNonNull(dependencyFileName);
        Objects.requireNonNull(dependencyFile);
        if(!testFileName.endsWith(".java") || !dependencyFileName.endsWith(".java"))
            throw new IllegalArgumentException("Not a java file");
    }

    public String idAsString(){
        return String.valueOf(id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TestBundle other && other.id() == id
                && testFileName.equals(other.testFileName)
                && dependencyFileName.equals(other.dependencyFileName)
                && Arrays.equals(testFile, other.testFile)
                && Arrays.equals(dependencyFile, other.dependencyFile);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id) ^ testFileName.hashCode() ^ dependencyFileName.hashCode()
                ^ Arrays.hashCode(testFile) ^ Arrays.hashCode(dependencyFile);
    }

    @Override
    public String toString() {
        return "TestFiles{" +
                "id=" + id +
                ", testFileName='" + testFileName + '\'' +
                ", testFile=" + Arrays.toString(testFile) +
                ", dependencyFileName='" + dependencyFileName + '\'' +
                ", dependencyFile=" + Arrays.toString(dependencyFile) +
                '}';
    }

}
