package io.github.andreaspapadakis.banking.backoffice.shared.validator;

import io.github.andreaspapadakis.banking.backoffice.shared.validation.AtLeastOneFieldPresent;
import jakarta.validation.ConstraintValidator;

public abstract class AtLeastOneFieldPresentValidator<T> implements
    ConstraintValidator<AtLeastOneFieldPresent, T> {}
