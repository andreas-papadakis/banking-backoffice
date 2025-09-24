package io.github.andreaspapadakis.banking.backoffice.accounts.dto;

import io.github.andreaspapadakis.banking.backoffice.accounts.validation.AllowedCurrency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AccountCreateRequest(
    @NotBlank(message = "{currencyBlankErrorMessage}")
    @Pattern(regexp = "^[A-Z]{3}$",
        message = "{currencyPatternErrorMessage}")
    @AllowedCurrency
    String currency) {
}
