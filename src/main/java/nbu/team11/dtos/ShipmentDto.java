package nbu.team11.dtos;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class ShipmentDto {
    private Integer id;
    private Integer employeeId;
    private Integer recipientId;
    private Integer senderId;
    private Integer recipientAddressId;
    private Integer senderAddressId;
    private Integer officeId;
    private double weight;
    private BigDecimal price;
    private Instant createdOn;
    private Instant updatedOn;
}
