package nbu.team11.dtos;


import lombok.Data;

import java.time.Instant;

@Data
public class CountryDto {
    private Integer id;
    private String name;
    private Instant createdOn;
    private Instant updatedOn;
}

