package nbu.team11.controllers.forms;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import nbu.team11.controllers.forms.constraints.PasswordConfirmed;
import nbu.team11.dtos.EmployeeDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.entities.enums.PositionType;
import nbu.team11.entities.enums.Role;

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

    //@TODO: Find a way to reuse the enum PositionType
    private PositionType position;

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

    public EmployeeDto toEmployeeDto() {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setPosition(this.position);
        employeeDto.setOfficeId(this.officeId);
        return employeeDto;
    }

    public UserDto toUserDto() {
        UserDto userDto = new UserDto();
        userDto.setRole(Role.EMPLOYEE);
        userDto.setFirstName(this.firstName);
        userDto.setLastName(this.lastName);
        userDto.setUsername(this.username);
        userDto.setEmail(this.email);
        userDto.setPassword(this.password);
        return userDto;
    }
}

