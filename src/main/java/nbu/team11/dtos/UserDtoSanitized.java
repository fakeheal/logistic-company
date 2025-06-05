package nbu.team11.dtos;

import lombok.Getter;
import lombok.Setter;
import nbu.team11.entities.enums.Role;

@Getter
@Setter
public class UserDtoSanitized {
    private String email;
    private Role role;
    private String firstName;
    private String lastName;
    private ClientDto clientDto;
    private EmployeeDto employeeDto;

    public UserDtoSanitized() {
    }

    public UserDtoSanitized(String email, Role role, EmployeeDto employeeDto) {
        this.email = email;
        this.role = role;
        this.employeeDto = employeeDto;
    }

    public UserDtoSanitized(String email, Role role, ClientDto clientDto) {
        this.email = email;
        this.role = role;
        this.clientDto = clientDto;
    }
}
