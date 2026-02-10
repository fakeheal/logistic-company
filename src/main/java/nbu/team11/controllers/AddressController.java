package nbu.team11.controllers;
import nbu.team11.dtos.*;
import nbu.team11.services.*;
import nbu.team11.services.exceptions.ResourceNotFound;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
public class AddressController {


    private final AddressService addressService;
    private final ClientService clientService;

    public AddressController(AddressService addressService, ClientService clientService) {
        this.addressService = addressService;
        this.clientService = clientService;
    }

    @GetMapping("/api/clients/{id}/addresses")
    @ResponseBody
    public List<AddressDto> apiRecipientAddresses(@PathVariable Integer id)
            throws ResourceNotFound {

        ClientDto client = clientService.getClientById(id);
        return client.getAddresses();
    }

}
