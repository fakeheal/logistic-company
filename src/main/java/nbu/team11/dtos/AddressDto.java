package nbu.team11.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
    private Integer id;                // new
    private String formattedAddress;
    private String coordinates;
}
