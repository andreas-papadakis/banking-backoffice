package io.github.andreaspapadakis.banking.backoffice.shared.exception;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ApiException {
  private final int errorCode;
  private final String message;
  private final LocalDateTime timestamp;

  ApiException(ErrorCode errorCode,
                      String message) {
    this.errorCode = errorCode.getCode();
    this.message = message;
    this.timestamp = LocalDateTime.now();
  }
}