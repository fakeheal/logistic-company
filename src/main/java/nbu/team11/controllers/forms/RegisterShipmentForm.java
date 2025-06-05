package nbu.team11.controllers.forms;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import nbu.team11.dtos.AddressDto;
import nbu.team11.dtos.ClientDto;

@Getter
@Setter
public class RegisterShipmentForm {
    @NotNull
    @Positive(message = "Weight must be positive")
    private Double weight;

    private boolean useProfileAddress;

    private AddressDto senderAddress;

    private boolean deliverToOffice;

    private Integer officeId;

    private AddressDto recipientAddress;

    private Integer recipientClientId;

    private Double price;
}
