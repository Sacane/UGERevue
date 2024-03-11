package fr.pentagon.ugeoverflow.controllers.dtos.responses;

import java.util.Objects;

public record TestResultDTO(
        boolean isSuccess,
        long amountValid,
        long amountFailures,
        String failuresDetails
) {
    public static String UNDEFINED_RESULT = "UNDEFINED";
    public TestResultDTO {
        Objects.requireNonNull(failuresDetails);
        if(amountValid < 0) throw new IllegalArgumentException("Must me positive");
        if(amountFailures < 0) throw new IllegalArgumentException("Must me positive");
    }

    public static TestResultDTO zero() {
        return new TestResultDTO(false, 0, 0, UNDEFINED_RESULT);
    }
    public boolean isZero() {
        return failuresDetails.equals(UNDEFINED_RESULT);
    }
}
