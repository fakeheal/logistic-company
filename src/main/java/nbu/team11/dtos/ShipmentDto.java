package nbu.team11.dtos;

import lombok.Data;
import nbu.team11.entities.Employee;
import nbu.team11.entities.Office;
import nbu.team11.entities.Client;
import nbu.team11.entities.Address;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ShipmentDto {
    private Integer id;
    private EmployeeDto employee;
    private ClientDto recipientClient;
    private AddressDto recipientAddress;
    private ClientDto senderClient;
    private AddressDto senderAddress;
    private OfficeDto sendingOffice;
    private OfficeDto receivingOffice;
    private double weight;
    private BigDecimal price;
    private Instant createdOn;
    private Instant updatedOn;
    private String uniqueID;
    private String status;
    private boolean payed;
}
