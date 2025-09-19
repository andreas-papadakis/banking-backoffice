package io.github.andreaspapadakis.banking.backoffice.shared.exception;

import java.time.LocalDateTime;

public record ApiException(String message, LocalDateTime timestamp) {}