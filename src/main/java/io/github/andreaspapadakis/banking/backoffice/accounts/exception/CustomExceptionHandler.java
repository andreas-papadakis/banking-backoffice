package io.github.andreaspapadakis.banking.backoffice.accounts.exception;

import io.github.andreaspapadakis.banking.backoffice.shared.exception.ApiException;
import io.github.andreaspapadakis.banking.backoffice.shared.utils.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  private final MessageSource messageSource;

  @ExceptionHandler(value = NoSuchElementException.class)
  public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiException(ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(value = RussianRouletteException.class)
  public ResponseEntity<Object> handleRussianRouletteException(RussianRouletteException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiException(ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiException> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {

    String requiredTypeName = (ex.getRequiredType() != null)
        ? ex.getRequiredType().getSimpleName()
        : "unknown";
    Object exceptionValue = ex.getValue() != null ? ex.getValue() : "null";
    String errorMessage = messageSource.getMessage("defaultMethodArgumentTypeMismatchErrorMessage",
        new Object[]{ex.getName(), exceptionValue, requiredTypeName},
        null);
    ApiException exception = new ApiException(errorMessage, LocalDateTime.now());

    return ResponseEntity.badRequest().body(exception);
  }

  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                             HttpHeaders headers,
                                                             HttpStatusCode status,
                                                             WebRequest request) {
    String handlerMethod = getHandlerMethodInfo(request);
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();

    String logMessage = Stream.concat(
        formatGlobalErrors(globalErrors),
        formatFieldErrors(fieldErrors)
    ).collect(Collectors.joining(", "));

    log.error("[{}]:[{}]", handlerMethod, logMessage);

    String globalErrorsMessage = globalErrors.stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(", "));

    String fieldErrorsMessage = fieldErrors.stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(", "));

    String errorMessage = Stream.of(globalErrorsMessage, fieldErrorsMessage)
        .filter(message -> !StringUtils.isNullOrBlank(message))
        .collect(Collectors.joining(", "));

    return ResponseEntity.badRequest().body(new ApiException(errorMessage, LocalDateTime.now()));
  }

  @Override
  public ResponseEntity<Object> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    String paramMessages = ex.getParameterValidationResults()
        .stream()
        .flatMap(parameterValidationResult -> parameterValidationResult
            .getResolvableErrors()
            .stream()
            .map(MessageSourceResolvable::getDefaultMessage))
        .collect(Collectors.joining(", "));

    String crossParamMessages = ex.getCrossParameterValidationResults()
        .stream()
        .map(MessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(", "));

    String allMessages = Stream.of(paramMessages, crossParamMessages)
        .filter(msg -> !msg.isBlank())
        .collect(Collectors.joining(", "));

    return ResponseEntity.badRequest().body(new ApiException(allMessages, LocalDateTime.now()));
  }

  private String getHandlerMethodInfo(WebRequest request) {
    try {
      Object handler = request.getAttribute(
          "org.springframework.web.servlet.HandlerMapping.bestMatchingHandler", 0);

      if (handler instanceof HandlerMethod handlerMethod) {
        return String.format("%s#%s",
            handlerMethod.getBeanType().getSimpleName(),
            handlerMethod.getMethod().getName());
      } else {
        return "unknown-handler";
      }
    } catch (Exception e) {
      // do nothing
    }

    return "unknown-handler";
  }

  private Stream<String> formatGlobalErrors(List<ObjectError> globalErrors) {
    return globalErrors.stream()
        .map(error -> String.format("[%s: %s]",
            error.getObjectName(),
            error.getDefaultMessage()));
  }

  private Stream<String> formatFieldErrors(List<FieldError> fieldErrors) {
    return fieldErrors.stream()
        .collect(Collectors.groupingBy(this::toFieldErrorKey,
            Collectors.mapping(FieldError::getDefaultMessage, Collectors.joining(", "))
        ))
        .entrySet()
        .stream()
        .map(entry -> String.format("[%s.%s=[%s]: %s]",
            entry.getKey().objectName(),
            entry.getKey().field(),
            entry.getKey().rejectedValue(),
            entry.getValue()));
  }

  private FieldErrorKey toFieldErrorKey(FieldError fieldError) {
    return new FieldErrorKey(
        fieldError.getObjectName(),
        fieldError.getField(),
        fieldError.getRejectedValue()
    );
  }

  private record FieldErrorKey(String objectName, String field, @Nullable Object rejectedValue) {}
}