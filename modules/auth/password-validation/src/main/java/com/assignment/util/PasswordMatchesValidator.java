package com.assignment.util;

import com.assignment.annotations.PasswordMatches;
import com.assignment.dtos.UserDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        // no initialization required
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) return true;

        UserDto user = (UserDto) obj;

        String password = user.getPassword();
        String matchingPassword = user.getMatchingPassword();

        if (password == null || matchingPassword == null) return false;

        return password.equals(matchingPassword);
    }
}
