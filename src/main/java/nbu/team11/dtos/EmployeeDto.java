package nbu.team11.dtos;

import lombok.Getter;
import lombok.Setter;
import nbu.team11.entities.enums.PositionType;

import java.time.Instant;


@Setter
@Getter
public class EmployeeDto {
    private Integer id;
    private PositionType position;
    private Instant createdOn;
    private Instant updatedOn;

    private String userFullName;
    private String userEmail;
    private String officeTitle;

    public EmployeeDto() {
    }
}
