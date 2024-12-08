package nbu.team11.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
public class OfficeDto {
    private String title;
    private Instant createdOn;
    private Instant updatedOn;
}
