package io.github.andreaspapadakis.banking.backoffice.accounts.exception;

import io.github.andreaspapadakis.banking.backoffice.shared.exception.ApiException;
import io.github.andreaspapadakis.banking.backoffice.shared.exception.ApiExceptionFactory;
import io.github.andreaspapadakis.banking.backoffice.shared.exception.ErrorCode;
import io.github.andreaspapadakis.banking.backoffice.shared.utils.StringUtils;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  private final ApiExceptionFactory apiExceptionFactory;

  @ExceptionHandler(value = NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ApiException handleNoSuchElementException(ServletWebRequest request) {
    return apiExceptionFactory.fromErrorCode(ErrorCode.ACCOUNT_NOT_FOUND, new Object[]{
        Objects.requireNonNull(request.getAttribute("id", 0))});
  }

  @ExceptionHandler(value = RussianRouletteException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  @ResponseBody
  public ApiException handleRussianRouletteException(ServletWebRequest request) {
    return apiExceptionFactory.fromErrorCode(ErrorCode.INSUFFICIENT_FUNDS, new Object[]{
        Objects.requireNonNull(request.getAttribute("id", 0))});
  }

  @ExceptionHandler(value = NoUpdatesException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  @ResponseBody
  public ApiException handleNoUpdatesException(ServletWebRequest request) {
    return apiExceptionFactory.fromErrorCode(ErrorCode.NO_UPDATES, new Object[]{
        Objects.requireNonNull(request.getAttribute("id", 0))});
  }

  @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ApiException handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    String requiredTypeName = (ex.getRequiredType() != null)
        ? ex.getRequiredType().getSimpleName()
        : "unknown";
    Object exceptionValue = ex.getValue() != null ? ex.getValue() : "null";
    return apiExceptionFactory.fromErrorCode(ErrorCode.INVALID_PARAMETER_TYPE, new Object[]{
        ex.getName(), exceptionValue, requiredTypeName});
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

    ApiException apiException = apiExceptionFactory.fromErrorCodeWithOverrideMessage(
        ErrorCode.METHOD_ARGUMENT_NOT_VALID, errorMessage);

    return ResponseEntity.badRequest().body(apiException);
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

    ApiException apiException = apiExceptionFactory.fromErrorCodeWithOverrideMessage(
        ErrorCode.METHOD_VALIDATION_FAILED, allMessages);

    return ResponseEntity.badRequest().body(apiException);
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