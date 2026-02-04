package nbu.team11.controllers.forms;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import nbu.team11.dtos.AddressDto;
import nbu.team11.dtos.ClientDto;
import nbu.team11.dtos.UserDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EditClientForm {
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

    @Valid
    private List<AddressFormEntry> addresses = new ArrayList<>();

    public UserDto toUserDto() {
        UserDto userDto = new UserDto();
        userDto.setFirstName(this.getFirstName());
        userDto.setLastName(this.getLastName());
        userDto.setEmail(this.getEmail());
        return userDto;
    }

    public ClientDto toClientDto() {
        ClientDto clientDto = new ClientDto();
        clientDto.setPhoneNumber(this.getPhoneNumber());

        List<AddressDto> addressDtos = new ArrayList<>();
        if (this.addresses != null) {
            for (AddressFormEntry entry : this.addresses) {
                if (entry.isMarkedForDeletion()) {
                    continue;
                }
                AddressDto addressDto = new AddressDto();
                addressDto.setId(entry.getId());
                addressDto.setFormattedAddress(entry.getFormattedAddress());
                addressDto.setCoordinates(entry.getCoordinates());
                addressDtos.add(addressDto);
            }
        }
        clientDto.setAddresses(addressDtos);

        return clientDto;
    }

    public List<Integer> getAddressIdsToDelete() {
        List<Integer> idsToDelete = new ArrayList<>();
        if (this.addresses != null) {
            for (AddressFormEntry entry : this.addresses) {
                if (entry.isMarkedForDeletion() && entry.getId() != null) {
                    idsToDelete.add(entry.getId());
                }
            }
        }
        return idsToDelete;
    }
}
