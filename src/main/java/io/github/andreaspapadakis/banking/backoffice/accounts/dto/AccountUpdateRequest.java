package io.github.andreaspapadakis.banking.backoffice.accounts.dto;

import io.github.andreaspapadakis.banking.backoffice.shared.validation.AllowedCurrency;
import io.github.andreaspapadakis.banking.backoffice.shared.validation.AtLeastOneFieldPresent;
import jakarta.validation.constraints.Pattern;
import org.jspecify.annotations.Nullable;

@AtLeastOneFieldPresent
public record AccountUpdateRequest(@Nullable
                                   Double balance,
                                   @Nullable
                                   @Pattern(regexp = "^[A-Z]{3}$",
                                       message = "{currencyPatternErrorMessage}")
                                   @AllowedCurrency
                                   String currency) {}
