package fr.pentagon.ugeoverflow.exception;

import fr.pentagon.revevue.common.exception.HttpException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(basePackages = "fr.pentagon.ugeoverflow.controllers.mvc")
public class MvcHttpExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(HttpException.class)
  public String handleHttpException(HttpException ex) {
    return switch (ex.getStatusCode()) {
      case 400 -> "error/400";
      case 401 -> "error/401";
      default -> "error/400";
    };
  }
}
