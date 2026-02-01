package nbu.team11.controllers.forms;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import nbu.team11.dtos.AddressDto;

import java.math.BigDecimal;

@Getter
@Setter
public class EmployeeRegisterShipmentForm {

    @NotNull
    @Positive(message = "Weight must be positive")
    private Double weight;

    // Sender information (selected by employee)
    private Integer senderClientId;
    private Integer sendingOfficeId;

    // Delivery options
    private boolean deliverToOffice;
    private Integer officeId;

    // Recipient info
    private AddressDto recipientAddress = new AddressDto();
    private Integer recipientClientId;
    private BigDecimal price;
}
