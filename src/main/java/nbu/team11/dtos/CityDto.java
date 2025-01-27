package nbu.team11.dtos;

import lombok.Data;

import java.time.Instant;

@Data
public class CityDto {
    private Integer id;
    private String name;
    private Integer countryId;
    private Instant createdOn;
    private Instant updatedOn;
}

