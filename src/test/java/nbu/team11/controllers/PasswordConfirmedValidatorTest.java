package nbu.team11.controllers;

import jakarta.validation.ConstraintValidatorContext;
import nbu.team11.controllers.forms.constraints.PasswordConfirmed;
import nbu.team11.controllers.forms.constraints.PasswordConfirmedValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PasswordConfirmedValidatorTest {

    private PasswordConfirmedValidator validator;

    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new PasswordConfirmedValidator();
        PasswordConfirmed annotation = mock(PasswordConfirmed.class);
        when(annotation.passwordField()).thenReturn("password");
        when(annotation.confirmPasswordField()).thenReturn("confirmPassword");

        validator.initialize(annotation);
        doNothing().when(context).disableDefaultConstraintViolation();
    }

    @Test
    void testValidPasswords() {
        Object form = new TestForm("securePassword", "securePassword");

        boolean result = validator.isValid(form, context);

        assertTrue(result, "Validator should return true when passwords match");
    }

    @Test
    void testNullPasswords() {
        Object form = new TestForm(null, null);

        boolean result = validator.isValid(form, context);

        assertFalse(result, "Validator should return false when both passwords are null");
    }

    @Test
    void testNullConfirmPassword() {
        Object form = new TestForm("securePassword", null);

        boolean result = validator.isValid(form, context);

        assertFalse(result, "Validator should return false when confirmPassword is null");
    }

    @Test
    void testNullPassword() {
        Object form = new TestForm(null, "securePassword");

        boolean result = validator.isValid(form, context);

        assertFalse(result, "Validator should return false when password is null");
    }

    private static class TestForm {
        private final String password;
        private final String confirmPassword;

        public TestForm(String password, String confirmPassword) {
            this.password = password;
            this.confirmPassword = confirmPassword;
        }

        public String getPassword() {
            return password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }
    }
}
