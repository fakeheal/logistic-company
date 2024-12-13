package nbu.team11.controllers.forms;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEmployeeForm extends EmployeeForm {
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
}
