package nbu.team11.dtos;

import lombok.Getter;
import lombok.Setter;
import nbu.team11.entities.enums.Role;

@Getter
@Setter
public class UserDto {
    private String password;
    private String email;
    private Role role;
    private String firstName;
    private String lastName;
    private String id;
    private ClientDto clientDto;
    private EmployeeDto employeeDto;
    private AddressDto addressDto;

    public UserDto() {

    }

    public UserDto(String email, String password, Role role, EmployeeDto employeeDto) {
        this.password = password;
        this.email = email;
        this.role = role;
        this.employeeDto = employeeDto;
    }

    public UserDto(String email, String password, Role role, ClientDto clientDto) {
        this.password = password;
        this.email = email;
        this.role = role;
        this.clientDto = clientDto;
    }

    //NOTE: Username is used by spring security in the user/login template
    public String getUsername(){
        return email;
    }

    public void setUsername(String username){
        this.email = username;
    }

}