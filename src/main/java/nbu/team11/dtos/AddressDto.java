package nbu.team11.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {

    private String formattedAddress;
    private String coordinates;
}
