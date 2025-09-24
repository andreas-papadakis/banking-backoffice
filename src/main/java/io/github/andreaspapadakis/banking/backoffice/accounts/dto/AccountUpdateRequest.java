package io.github.andreaspapadakis.banking.backoffice.accounts.dto;

import io.github.andreaspapadakis.banking.backoffice.accounts.validation.AllowedCurrency;
import io.github.andreaspapadakis.banking.backoffice.accounts.validation.AtLeastOneFieldPresent;
import jakarta.validation.constraints.Pattern;

@AtLeastOneFieldPresent
public record AccountUpdateRequest(Double balance,
                                   @Pattern(regexp = "^[A-Z]{3}$",
                                       message = "{currencyPatternErrorMessage}")
                                   @AllowedCurrency
                                   String currency) {}
