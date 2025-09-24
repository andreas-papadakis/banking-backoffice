package io.github.andreaspapadakis.banking.backoffice.accounts.dto;

import java.util.Date;
import java.util.UUID;

public record AccountResponseDto(UUID id,
                                 Double balance,
                                 String currency,
                                 Date createdAt) {}
