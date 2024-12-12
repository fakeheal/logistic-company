package nbu.team11.dtos;

import lombok.Getter;
import lombok.Setter;
import nbu.team11.entities.enums.Role;

@Getter
@Setter
public class UserDto {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private Role role;
    private String firstName;
    private String lastName;

    public UserDto() {

    }

    public UserDto(String username, String password, String email, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public String getFullName() {
        return this.getFirstName() + " " + this.getLastName();
    }
}