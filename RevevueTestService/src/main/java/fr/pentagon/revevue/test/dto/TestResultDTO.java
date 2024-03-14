package fr.pentagon.revevue.test.dto;

import java.util.Objects;

public record TestResultDTO(
        boolean isSuccess,
        long amountValid,
        long amountFailures,
        String failuresDetails
) {
    public TestResultDTO {
        Objects.requireNonNull(failuresDetails);
        if(amountValid < 0) throw new IllegalArgumentException("Must me positive");
        if(amountFailures < 0) throw new IllegalArgumentException("Must me positive");
    }
}
