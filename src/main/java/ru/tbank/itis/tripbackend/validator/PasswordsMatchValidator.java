package ru.tbank.itis.tripbackend.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.tbank.itis.tripbackend.annotation.PasswordsMatch;
import ru.tbank.itis.tripbackend.dto.request.UserRegistrationRequest;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, UserRegistrationRequest> {

    @Override
    public boolean isValid(UserRegistrationRequest request, ConstraintValidatorContext context) {
        if (request.password() == null || request.repeatPassword() == null) {
            return true;
        }
        return request.password().equals(request.repeatPassword());
    }
}