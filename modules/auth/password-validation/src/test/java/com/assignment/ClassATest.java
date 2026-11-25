package com.assignment;

import com.assignment.annotations.PasswordMatches;
import com.assignment.dtos.UserDto;
import com.assignment.util.PasswordMatchesValidator;
import jakarta.validation.Constraint;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static org.junit.jupiter.api.Assertions.*;

public class ClassATest {

    private UserDto buildUser(String password, String matchingPassword) {
        UserDto dto = new UserDto();
        dto.setEmail("a@b.com");
        dto.setFirstName("A");
        dto.setLastName("B");
        dto.setPassword(password);
        dto.setMatchingPassword(matchingPassword);
        return dto;
    }

    // ---------------- PasswordMatchesValidator.isValid() tests ----------------

    @Test
    void isValid_shouldReturnTrue_whenPasswordAndMatchingPasswordAreSame() {
        PasswordMatchesValidator validator = new PasswordMatchesValidator();
        UserDto dto = buildUser("pass123", "pass123");

        boolean result = validator.isValid(dto, null);

        assertTrue(result);
    }

    @Test
    void isValid_shouldReturnFalse_whenPasswordAndMatchingPasswordAreDifferent() {
        PasswordMatchesValidator validator = new PasswordMatchesValidator();
        UserDto dto = buildUser("pass123", "pass124");

        boolean result = validator.isValid(dto, null);

        assertFalse(result);
    }

    @Test
    void isValid_shouldNotThrow_andReturnFalse_whenPasswordIsNull() {
        PasswordMatchesValidator validator = new PasswordMatchesValidator();
        UserDto dto = buildUser(null, "pass123");

        boolean result = assertDoesNotThrow(() -> validator.isValid(dto, null));
        assertFalse(result);
    }

    @Test
    void isValid_shouldNotThrow_andReturnFalse_whenMatchingPasswordIsNull() {
        PasswordMatchesValidator validator = new PasswordMatchesValidator();
        UserDto dto = buildUser("pass123", null);

        boolean result = assertDoesNotThrow(() -> validator.isValid(dto, null));
        assertFalse(result);
    }

    // ---------------- PasswordMatches annotation wiring tests ----------------

    @Test
    void userDto_shouldBeAnnotatedWithPasswordMatches() {
        assertTrue(UserDto.class.isAnnotationPresent(PasswordMatches.class));
    }

    @Test
    void passwordMatches_shouldHaveRetentionRuntime() {
        Retention retention = PasswordMatches.class.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    void passwordMatches_shouldTargetType_andAnnotationType() {
        Target target = PasswordMatches.class.getAnnotation(Target.class);
        assertNotNull(target);

        assertTrue(Arrays.asList(target.value()).contains(TYPE));
        assertTrue(Arrays.asList(target.value()).contains(ANNOTATION_TYPE));
    }

    @Test
    void passwordMatches_shouldBeConstraintValidatedByPasswordMatchesValidator() {
        Constraint constraint = PasswordMatches.class.getAnnotation(Constraint.class);
        assertNotNull(constraint);

        Class<?>[] validatedBy = constraint.validatedBy();
        assertNotNull(validatedBy);
        assertTrue(Arrays.asList(validatedBy).contains(PasswordMatchesValidator.class));
    }
}
