package nbu.team11.controllers.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOfficeForm {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 20, message = "Title must be between 5 and 20 characters")
    private String title;

    @NotBlank(message = "Address is required")
    private String formattedAddress;

    @NotBlank(message = "Coordinates are required")
    private String coordinates;
}
