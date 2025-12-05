package nbu.team11.controllers.forms;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import nbu.team11.dtos.AddressDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RegisterShipmentForm {

    @NotNull
    @Positive(message = "Weight must be positive")
    private Double weight;

    /**
     * If true, the user will be filling a new address in senderAddress and that one should be used.
     * If false, the UI should present a dropdown populated from `addresses` and submit
     * `selectedSenderAddressId` to indicate which existing address to use.
     */
    private boolean createNewAddress = false;

    // Used when the user chooses to create a new sender address
    private AddressDto senderAddress = new AddressDto();

    // If the user chooses an existing address from the dropdown,
    // this should contain the selected Address id (AddressDto.id).
    private Integer selectedSenderAddressId;

    // The list of existing addresses for the client (populated by controller)
    private List<AddressDto> addresses = new ArrayList<>();

    private boolean deliverToOffice;
    private Integer officeId;

    // Recipient info (kept as before)
    private AddressDto recipientAddress = new AddressDto();
    private Integer recipientClientId;
    private BigDecimal price;
}
