package com.packages.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({UserNotFoundException.class, PictureNotFoundException.class, MessageNotFoundException.class})
  public ResponseEntity<String> handleNotFound(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .contentType(MediaType.TEXT_PLAIN)
      .body(exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleInvalidPayload(MethodArgumentNotValidException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .contentType(MediaType.TEXT_PLAIN)
      .body(firstErrorMessage(exception));
  }

  /**
   * Returns the message of the first rejected field, so that the client keeps displaying a single
   * reason like it did when the checks were chained in the service.
   *
   * @param exception exception raised by the validation of a request payload
   * @return the message to display
   */
  private String firstErrorMessage(MethodArgumentNotValidException exception) {
    return exception.getBindingResult().getFieldErrors().stream()
      .map(FieldError::getDefaultMessage)
      .findFirst()
      .orElse("Invalid request");
  }
}
