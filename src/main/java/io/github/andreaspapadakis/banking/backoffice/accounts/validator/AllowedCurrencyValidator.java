package io.github.andreaspapadakis.banking.backoffice.accounts.validator;

import io.github.andreaspapadakis.banking.backoffice.accounts.validation.AllowedCurrency;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Currency;

public class AllowedCurrencyValidator implements ConstraintValidator<AllowedCurrency, String> {
  @Override
  public boolean isValid(String currency, ConstraintValidatorContext context) {
    if (currency == null) {
      return true; // @NotBlank handles null/empty
    }

    try {
      Currency.getInstance(currency);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
