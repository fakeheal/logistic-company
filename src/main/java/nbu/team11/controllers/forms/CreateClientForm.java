package nbu.team11.controllers.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import nbu.team11.dtos.ClientDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.entities.enums.Role;

@Getter
@Setter
public class CreateClientForm {
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 10, max = 13)
    private String phoneNumber;

    public UserDto toUserDto() {
        UserDto userDto = new UserDto();
        userDto.setRole(Role.CLIENT);
        userDto.setFirstName(this.getFirstName());
        userDto.setLastName(this.getLastName());
        userDto.setEmail(this.getEmail());
        return userDto;
    }

    public ClientDto toClientDto() {
        ClientDto clientDto = new ClientDto();
        clientDto.setPhoneNumber(this.getPhoneNumber());
        return clientDto;
    }
}
