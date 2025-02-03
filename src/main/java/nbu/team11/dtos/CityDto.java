package nbu.team11.dtos;

import lombok.Data;

import java.time.Instant;

import jakarta.validation.constraints.*;

@Data
public class CityDto {
    private Integer id;

    @NotBlank(message = "City name cannot be blank!")
    @Size(min = 3, max = 20, message = "City name has to be between 3 and 20 characters!")
    private String name;

    private Integer countryId;
    private Instant createdOn;
    private Instant updatedOn;
}
