package nbu.team11.controllers;

import jakarta.validation.Valid;
import nbu.team11.controllers.forms.AddressFormEntry;
import nbu.team11.controllers.forms.CreateClientForm;
import nbu.team11.controllers.forms.EditClientForm;
import nbu.team11.dtos.AddressDto;
import nbu.team11.dtos.ClientDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.entities.enums.Role;
import nbu.team11.services.ClientService;
import nbu.team11.services.CustomPermissionEvaluator;
import nbu.team11.services.UserService;
import nbu.team11.services.exceptions.AddressLinkedToShipment;
import nbu.team11.services.exceptions.CannotDeleteClient;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.ResourceNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ClientController extends AbstractBaseController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final ClientService clientService;

    public ClientController(CustomPermissionEvaluator permissionEvaluator, UserService userService, ClientService clientService) {
        super(permissionEvaluator);
        this.userService = userService;
        this.clientService = clientService;
    }

    @RequestMapping("/api/clients/getAllClients")
    @GetMapping
    public List<ClientDto> getAllClients() {
        return clientService.getAllClients();
    }

    @RequestMapping("/client")
    public String index(
            Model model,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (!this.permissionEvaluator.canManageClients(authentication)) {
            return "redirect:/home";
        }

        Page<UserDto> clients = userService.paginate(page, size, Role.CLIENT);

        model.addAttribute("title", "All clients");
        model.addAttribute("content", "client/index");
        model.addAttribute("clients", clients);

        return "layouts/app";
    }

    @GetMapping("/client/create")
    public String create(Model model, Authentication authentication) {
        if (!this.permissionEvaluator.canManageClients(authentication)) {
            return "redirect:/home";
        }

        model.addAttribute("title", "Create Client");
        model.addAttribute("content", "client/create");

        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new CreateClientForm());
        }

        return "layouts/app";
    }

    @PostMapping("/client/store")
    public String store(
            @Valid CreateClientForm createClientForm,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes attributes
    ) {
        if (!this.permissionEvaluator.canManageClients(authentication)) {
            return "redirect:/home";
        }

        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            attributes.addFlashAttribute("form", createClientForm);
            return "redirect:/client/create";
        }

        try {
            clientService.createClientWithGeneratedPassword(
                    createClientForm.toClientDto(),
                    createClientForm.toUserDto()
            );
        } catch (EmailNotAvailable e) {
            bindingResult.rejectValue("email", "error.email", "Email is already in use");
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            attributes.addFlashAttribute("form", createClientForm);
            return "redirect:/client/create";
        }

        attributes.addFlashAttribute("successMessage", "Client created successfully. Password has been sent to their email.");
        return "redirect:/client";
    }

    @GetMapping("/api/client/current")
    @ResponseBody
    public ClientDto getCurrentClient(Authentication authentication)
            throws ResourceNotFound {
        return clientService.getClientByEmail(authentication.getName());
    }

    @GetMapping("/client/{id}")
    public String view(
            @PathVariable Integer id,
            Model model,
            Authentication authentication
    ) {
        if (!this.permissionEvaluator.canManageClients(authentication)) {
            return "redirect:/home";
        }

        try {
            ClientDto client = clientService.getClientById(id);
            model.addAttribute("title", "View Client");
            model.addAttribute("content", "client/view");
            model.addAttribute("client", client);

            java.util.Map<Integer, Boolean> addressLinkedToShipment = new java.util.HashMap<>();
            if (client.getAddresses() != null) {
                for (AddressDto address : client.getAddresses()) {
                    if (address.getId() != null) {
                        addressLinkedToShipment.put(address.getId(), clientService.isAddressLinkedToShipment(address.getId()));
                    }
                }
            }
            model.addAttribute("addressLinkedToShipment", addressLinkedToShipment);

            return "layouts/app";
        } catch (ResourceNotFound e) {
            return "redirect:/client";
        }
    }

    @GetMapping("/client/{id}/edit")
    public String edit(
            @PathVariable Integer id,
            Model model,
            Authentication authentication
    ) {
        if (!this.permissionEvaluator.canManageClients(authentication)) {
            return "redirect:/home";
        }

        try {
            ClientDto client = clientService.getClientById(id);

            EditClientForm editClientForm = new EditClientForm();
            editClientForm.setFirstName(client.getUserFirstName());
            editClientForm.setLastName(client.getUserLastName());
            editClientForm.setEmail(client.getUserEmail());
            editClientForm.setPhoneNumber(client.getPhoneNumber());

            if (client.getAddresses() != null) {
                for (AddressDto addressDto : client.getAddresses()) {
                    AddressFormEntry entry = new AddressFormEntry();
                    entry.setId(addressDto.getId());
                    entry.setFormattedAddress(addressDto.getFormattedAddress());
                    entry.setCoordinates(addressDto.getCoordinates());
                    entry.setMarkedForDeletion(false);
                    editClientForm.getAddresses().add(entry);
                }
            }

            model.addAttribute("title", "Edit Client");
            model.addAttribute("content", "client/edit");
            model.addAttribute("client", client);

            java.util.Map<Integer, Boolean> addressLinkedToShipment = new java.util.HashMap<>();
            if (client.getAddresses() != null) {
                for (AddressDto address : client.getAddresses()) {
                    if (address.getId() != null) {
                        addressLinkedToShipment.put(address.getId(), clientService.isAddressLinkedToShipment(address.getId()));
                    }
                }
            }
            model.addAttribute("addressLinkedToShipment", addressLinkedToShipment);

            if (!model.containsAttribute("form")) {
                model.addAttribute("form", editClientForm);
            }

            return "layouts/app";
        } catch (ResourceNotFound e) {
            return "redirect:/client";
        }
    }

    @PostMapping("/client/{id}/update")
    public String update(
            @PathVariable Integer id,
            @Valid EditClientForm editClientForm,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes attributes
    ) {
        if (!this.permissionEvaluator.canManageClients(authentication)) {
            return "redirect:/home";
        }

        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            attributes.addFlashAttribute("form", editClientForm);
            return "redirect:/client/" + id + "/edit";
        }

        try {
            clientService.updateClientWithUser(
                    id,
                    editClientForm.toClientDto(),
                    editClientForm.toUserDto(),
                    editClientForm.getAddressIdsToDelete()
            );
            attributes.addFlashAttribute("successMessage", "Client updated successfully.");
            return "redirect:/client/" + id;
        } catch (ResourceNotFound e) {
            attributes.addFlashAttribute("errorMessage", "Client not found.");
            return "redirect:/client";
        } catch (EmailNotAvailable e) {
            bindingResult.rejectValue("email", "error.email", "Email is already in use");
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            attributes.addFlashAttribute("form", editClientForm);
            return "redirect:/client/" + id + "/edit";
        } catch (AddressLinkedToShipment e) {
            attributes.addFlashAttribute("errorMessage", e.getMessage());
            attributes.addFlashAttribute("form", editClientForm);
            return "redirect:/client/" + id + "/edit";
        }
    }

    @PostMapping("/client/{id}/delete")
    public String delete(
            @PathVariable Integer id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (!this.permissionEvaluator.canManageClients(authentication)) {
            return "redirect:/home";
        }

        try {
            UserDto currentUser = userService.getByEmail(authentication.getName());
            Integer currentUserId = Integer.parseInt(currentUser.getId());
            clientService.anonymizeClient(id, currentUserId);
            redirectAttributes.addFlashAttribute("successMessage", "Client data has been anonymized successfully.");
        } catch (ResourceNotFound e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Client not found.");
        } catch (CannotDeleteClient e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/client";
    }
}
