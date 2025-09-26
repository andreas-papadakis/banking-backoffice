package io.github.andreaspapadakis.banking.backoffice.shared.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiExceptionFactory {
  private final MessageSource messageSource;

  public ApiException fromErrorCode(ErrorCode errorCode, Object[] messageArgs) {
    String message = messageSource.getMessage(errorCode.getMessageKey(), messageArgs, null);
    return new ApiException(errorCode, message);
  }

  public ApiException fromErrorCodeWithOverrideMessage(ErrorCode errorCode, String message) {
    return new ApiException(errorCode, message);
  }
}
