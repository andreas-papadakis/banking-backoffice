package io.github.andreaspapadakis.banking.backoffice.accounts.dto;

import java.util.Date;

public record AccountResponseDto(String id,
                                 Double balance,
                                 String currency,
                                 Date createdAt) {}
