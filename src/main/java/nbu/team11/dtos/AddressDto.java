package nbu.team11.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
    private String street;
    private String postalCode;
    private String cityName; // optional, if you want city title instead of full object
}
