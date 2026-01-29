package nbu.team11.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClientDto {
    private Integer id;
    private String phoneNumber;
    private String userFullName;

    private List<AddressDto> addresses = new ArrayList<>();

    public ClientDto() {
    }

    public ClientDto(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
