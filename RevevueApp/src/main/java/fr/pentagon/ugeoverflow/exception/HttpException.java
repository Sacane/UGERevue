package fr.pentagon.ugeoverflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpException extends RuntimeException{
    private final int statusCode;
    public HttpException(HttpStatus httpStatus, String message){
        super(message);
        this.statusCode = httpStatus.value();
    }
    public HttpException(int code, String msg){
        super(msg);
        this.statusCode = code;
    }

    public HttpException(int code){
        super(getDefaultMessage(code));
        this.statusCode = code;
    }
    public static HttpException notFound(String msg){
        return new HttpException(HttpStatus.NOT_FOUND, msg);
    }
    public static HttpException forbidden(String msg){
        return new HttpException(HttpStatus.FORBIDDEN, msg);
    }
    public static HttpException badRequest(String msg){
        return new HttpException(HttpStatus.BAD_REQUEST, msg);
    }
    public static HttpException unauthorized(String msg){
        return new HttpException(HttpStatus.UNAUTHORIZED, msg);
    }

    public int getStatusCode() {
        return statusCode;
    }

    private static String getDefaultMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "HTTP Error";
        };
    }
    public ResponseEntity<?> toResponseEntity(){
        return ResponseEntity.status(statusCode).body(new HttpError(statusCode, getMessage()));
    }
}
