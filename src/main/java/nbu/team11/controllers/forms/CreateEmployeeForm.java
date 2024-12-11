package nbu.team11.controllers.forms;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import nbu.team11.controllers.forms.constraints.PasswordConfirmed;

@Getter
@Setter
@PasswordConfirmed(passwordField = "password", confirmPasswordField = "confirmPassword", message = "Passwords must match")
public class CreateEmployeeForm {
    @NotBlank
    @Size(min = 3, max = 50)
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 50)
    private String lastName;

    @NotBlank
    //@TODO: Find a way to reuse the enum PositionType
    @Pattern(regexp = "^(ADMIN|COORDINATOR|DELIVERYMAN)$")
    private String position;

    @NotNull
    private Integer officeId;

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;
}

