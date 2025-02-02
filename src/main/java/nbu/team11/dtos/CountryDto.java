package nbu.team11.dtos;

import lombok.Data;

import java.time.Instant;

import jakarta.validation.constraints.*;

@Data
public class CountryDto {
    private Integer id;

    @NotBlank(message = "Country name cannot be blank!")
    @Size(min = 5, max = 10, message = "Country name has to be between 5 and 10 characters!")
    private String name;
    private Instant createdOn;
    private Instant updatedOn;
}
