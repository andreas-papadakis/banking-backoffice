package io.github.andreaspapadakis.banking.backoffice.shared.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  // 1000-1999: Account related errors
  ACCOUNT_NOT_FOUND(1000, "accountNotExistsErrorMessage"),
  INSUFFICIENT_FUNDS(1001, "insufficientFundsErrorMessage"),
  NO_UPDATES(1002, "noUpdatesErrorMessage"),

  // 4000-4999 System errors
  INVALID_PARAMETER_TYPE(4400, "defaultMethodArgumentTypeMismatchErrorMessage"),
  METHOD_ARGUMENT_NOT_VALID(4401, ""),
  METHOD_VALIDATION_FAILED(4402, "");

  private final int code;
  private final String messageKey;
}
