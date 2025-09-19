package io.github.andreaspapadakis.banking.backoffice.exception;

import java.time.LocalDateTime;

public record ApiException(String message, LocalDateTime timestamp) {}