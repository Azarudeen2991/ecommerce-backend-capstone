package com.assignment;

import com.assignment.annotations.ValidEmail;
import com.assignment.util.EmailValidator;
import jakarta.validation.Constraint;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.jupiter.api.Assertions.*;

public class ClassATest {

    @Test
    void emailValidator_shouldReturnTrue_forValidEmails() {
        EmailValidator validator = new EmailValidator();

        assertTrue(validator.isValid("test@example.com", null));
        assertTrue(validator.isValid("john.doe@example.com", null));
        assertTrue(validator.isValid("john_doe123@example.co.in", null));
        assertTrue(validator.isValid("a-b+c@domain.com", null));
    }

    @Test
    void emailValidator_shouldReturnFalse_forInvalidEmails() {
        EmailValidator validator = new EmailValidator();

        assertFalse(validator.isValid("plainaddress", null));
        assertFalse(validator.isValid("missing@domain", null));
        assertFalse(validator.isValid("@missinglocal.com", null));
        assertFalse(validator.isValid("test@.com", null));
        assertFalse(validator.isValid("test@domain..com", null));
        assertFalse(validator.isValid("test@domain,com", null));
        assertFalse(validator.isValid("test domain@domain.com", null));
    }

    @Test
    void emailValidator_shouldReturnFalse_forNullOrBlank() {
        EmailValidator validator = new EmailValidator();

        assertDoesNotThrow(() -> validator.isValid(null, null));
        assertFalse(validator.isValid(null, null));
        assertFalse(validator.isValid("", null));
        assertFalse(validator.isValid("   ", null));
    }

    @Test
    void validEmail_shouldHaveRetentionRuntime() {
        Retention retention = ValidEmail.class.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(RUNTIME, retention.value());
    }

    @Test
    void validEmail_shouldBeDocumented() {
        Documented documented = ValidEmail.class.getAnnotation(Documented.class);
        assertNotNull(documented);
    }

    @Test
    void validEmail_shouldTargetTypeFieldAndAnnotationType() {
        Target target = ValidEmail.class.getAnnotation(Target.class);
        assertNotNull(target);

        assertTrue(Arrays.asList(target.value()).contains(TYPE));
        assertTrue(Arrays.asList(target.value()).contains(FIELD));
        assertTrue(Arrays.asList(target.value()).contains(ANNOTATION_TYPE));
    }

    @Test
    void validEmail_shouldBeConstraintValidatedByEmailValidator() {
        Constraint constraint = ValidEmail.class.getAnnotation(Constraint.class);
        assertNotNull(constraint);

        Class<?>[] validatedBy = constraint.validatedBy();
        assertNotNull(validatedBy);
        assertTrue(Arrays.asList(validatedBy).contains(EmailValidator.class));
    }

    @Test
    void validEmail_defaultMessageShouldBeInvalidEmail() {
        assertEquals("Invalid email", new ValidEmail() {
            @Override public Class<? extends java.lang.annotation.Annotation> annotationType() { return ValidEmail.class; }
            @Override public String message() { return "Invalid email"; }
            @Override public Class<?>[] groups() { return new Class<?>[0]; }
            @Override public Class<? extends jakarta.validation.Payload>[] payload() { return new Class[0]; }
        }.message());
    }
}
