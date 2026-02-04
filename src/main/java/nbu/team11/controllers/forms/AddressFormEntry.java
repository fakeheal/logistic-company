package nbu.team11.controllers.forms;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressFormEntry {
    private Integer id;

    @NotBlank(message = "Address cannot be blank")
    private String formattedAddress;

    @NotBlank(message = "Coordinates cannot be blank")
    private String coordinates;

    private boolean markedForDeletion;
}
