package nbu.team11.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import jakarta.validation.constraints.*;

@Getter
@Setter
public class OfficeDto {
    private Integer id;

    @NotBlank(message = "Title cannot be blank!")
    private String title;
    private AddressDto addressDto;
    private Instant createdOn;
    private Instant updatedOn;
}
