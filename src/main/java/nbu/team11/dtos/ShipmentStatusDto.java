package nbu.team11.dtos;

import lombok.Data;
import nbu.team11.entities.enums.Status;

import java.time.Instant;

@Data
public class ShipmentStatusDto {
    private Integer id;
    private Integer shipmentId;
    private Status status;
    private String comment;
    private Instant createdOn;
    private Instant updatedOn;
}
