package fr.pentagon.ugeoverflow.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class HttpExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<?> handleHttpException(HttpException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatusCode.valueOf(ex.getStatusCode()));
    }
}
