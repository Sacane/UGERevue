package fr.pentagon.revevue.test.exception;

import fr.pentagon.revevue.common.exception.HttpException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(basePackages = "fr.pentagon.revevue.test")
public class HttpExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(HttpException.class)
    public ResponseEntity<?> handleHttpException(HttpException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatusCode.valueOf(ex.getStatusCode()));
    }
}
