package nbu.team11.controllers.forms.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class PasswordConfirmedValidator implements ConstraintValidator<PasswordConfirmed, Object> {

    private String passwordField;
    private String confirmPasswordField;

    @Override
    public void initialize(PasswordConfirmed constraintAnnotation) {
        this.passwordField = constraintAnnotation.passwordField();
        this.confirmPasswordField = constraintAnnotation.confirmPasswordField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object password = new BeanWrapperImpl(value)
                .getPropertyValue(passwordField);
        Object confirmPassword = new BeanWrapperImpl(value)
                .getPropertyValue(confirmPasswordField);

        if (password == null || confirmPassword == null) {
            return false;
        }

        if (!password.equals(confirmPassword)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(passwordField)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
