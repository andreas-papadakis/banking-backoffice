package io.github.andreaspapadakis.banking.backoffice.accounts.validation;

import io.github.andreaspapadakis.banking.backoffice.accounts.validator.AtLeastOneFieldPresentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = AtLeastOneFieldPresentValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneFieldPresent {

  String message() default "{atLeastOneFieldRequiredErrorMessage}";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

}
