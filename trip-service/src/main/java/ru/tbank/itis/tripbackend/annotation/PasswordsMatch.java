package ru.tbank.itis.tripbackend.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.tbank.itis.tripbackend.validator.PasswordsMatchValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordsMatchValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordsMatch {
    String message() default "Пароли не совпадают";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}