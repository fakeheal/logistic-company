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

@Data
public class ShipmentDto {
    @Getter
    private Integer id;

    private Employee employee;
    private Client recipient;
    private Address recipientAddress;
    private Client sender;
    private Address senderAddress;
    private Office office;
    private double weight;
    private BigDecimal price;
    private Instant createdOn;
    private Instant updatedOn;
    @Getter
    private String uniqueID;
    @Setter
    private String status;
}
