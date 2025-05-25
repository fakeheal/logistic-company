package nbu.team11.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDto {
    private String phoneNumber;
    private AddressDto addressDto;

    public ClientDto() {
    }

    public ClientDto(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
