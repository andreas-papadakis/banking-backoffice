package io.github.andreaspapadakis.banking.backoffice.accounts.validator;

import io.github.andreaspapadakis.banking.backoffice.accounts.dto.AccountUpdateRequest;
import io.github.andreaspapadakis.banking.backoffice.shared.utils.StringUtils;
import io.github.andreaspapadakis.banking.backoffice.shared.validator.AtLeastOneFieldPresentValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneAccountFieldPresentValidator extends
    AtLeastOneFieldPresentValidator<AccountUpdateRequest> {
  @Override
  public boolean isValid(AccountUpdateRequest accountUpdateRequest,
                         ConstraintValidatorContext context) {
    if (accountUpdateRequest == null) {
      return true; // handled elsewhere
    }

    return accountUpdateRequest.balance() != null
        || !StringUtils.isNullOrEmpty(accountUpdateRequest.currency());
  }
}
