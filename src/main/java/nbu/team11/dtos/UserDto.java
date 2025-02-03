package nbu.team11.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import nbu.team11.entities.enums.Role;

@Getter
@Setter
public class UserDto {
    private Integer id;

    @NotBlank(message = "Username cannot be blank!")
    @Size(min = 5, max = 20, message = "Username has to be between 5 and 20 characters!")
    private String username;

    @NotBlank(message = "Password cannot be blank!")
    private String password;

    @NotBlank(message = "Email cannot be blank!")
    @Email(message = "Invalid email address. Please enter a proper email!")
    private String email;

    private Role role;

    @NotBlank(message = "First Name cannot be blank!")
    @Size(max = 20, message = "First name has to be up to 20 characters!")
    private String firstName;

    @NotBlank(message = "Last Name cannot be blank!")
    @Size(max = 20, message = "Last name has to be up to 20 characters!")
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