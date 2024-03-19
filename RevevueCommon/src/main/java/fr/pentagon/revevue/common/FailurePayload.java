package fr.pentagon.revevue.common;

import java.util.Arrays;

public enum FailurePayload {
    COMPILATION_ERROR(800), UNSUPPORTED_FORMAT_FILE(799),UNKNOWN(0);

    private final int code;
    FailurePayload(int code){
        this.code = code;
    }
    public int code(){return code;}

    public static FailurePayload fromCode(int code) {
        return Arrays.stream(FailurePayload.values()).filter(payload -> payload.code == code).findFirst().orElse(UNKNOWN);
    }
}
