package io.github.andreaspapadakis.banking.backoffice.exception;

import io.github.andreaspapadakis.banking.backoffice.accounts.exception.RussianRouletteException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = LogManager.getLogger(CustomExceptionHandler.class);

  @ExceptionHandler(value = NoSuchElementException.class)
  public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException ex) {
    return new ResponseEntity<>(new ApiException(ex.getMessage(),
            LocalDateTime.now()),
            HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = RussianRouletteException.class)
  public ResponseEntity<Object> handleRussianRouletteException(RussianRouletteException ex) {
    return new ResponseEntity<>(new ApiException(ex.getMessage(),
            LocalDateTime.now()),
            HttpStatus.FORBIDDEN);
  }
}