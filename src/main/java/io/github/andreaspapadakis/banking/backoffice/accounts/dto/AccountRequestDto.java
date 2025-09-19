package io.github.andreaspapadakis.banking.backoffice.accounts.dto;

public record AccountRequestDto(String id,
                                Double balance,
                                String currency) {}
