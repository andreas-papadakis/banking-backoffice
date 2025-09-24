package io.github.andreaspapadakis.banking.backoffice.shared.validator;

import io.github.andreaspapadakis.banking.backoffice.shared.utils.StringUtils;
import io.github.andreaspapadakis.banking.backoffice.shared.validation.AllowedCurrency;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Currency;

public class AllowedCurrencyValidator implements ConstraintValidator<AllowedCurrency, String> {
  @Override
  public boolean isValid(String currency, ConstraintValidatorContext context) {
    if (StringUtils.isNullOrEmpty(currency)) {
      return true; // @NotBlank should handle null/empty
    }

    try {
      Currency.getInstance(currency);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
