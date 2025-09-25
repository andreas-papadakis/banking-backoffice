package io.github.andreaspapadakis.banking.backoffice.accounts.dto;

import java.util.Date;
import java.util.UUID;

public record AccountResponse(UUID id,
                              double balance,
                              String currency,
                              Date createdAt) {}
