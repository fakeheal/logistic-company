package nbu.team11.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDto {
    private String phoneNumber;

    public ClientDto() {
    }

    public ClientDto(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
