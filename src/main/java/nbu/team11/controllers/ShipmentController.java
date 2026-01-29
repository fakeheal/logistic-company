package nbu.team11.controllers;

import nbu.team11.controllers.forms.EmployeeRegisterShipmentForm;
import nbu.team11.controllers.forms.RegisterShipmentForm;
import nbu.team11.dtos.*;
import nbu.team11.entities.Address;
import nbu.team11.entities.Shipment;
import nbu.team11.entities.enums.Role;
import nbu.team11.entities.enums.Status;
import nbu.team11.services.*;
import nbu.team11.services.exceptions.ResourceNotFound;
import nbu.team11.services.exceptions.ShipmentNotFound;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles UI pages and REST endpoints for shipments.
 */
@Controller
@RequestMapping
public class ShipmentController {

    private final ClientService clientService;
    private final OfficeService officeService;
    private final ShipmentService shipmentService;
    private final AddressService addressService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public ShipmentController(ClientService clientService,
                              OfficeService officeService,
                              ShipmentService shipmentService,
                              ModelMapper modelMapper,
                              UserService userService,
                              AddressService addressService) {
        this.clientService = clientService;
        this.officeService = officeService;
        this.shipmentService = shipmentService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.addressService = addressService;
    }

    private List<ShipmentDto> mapAndSetStatus(List<Shipment> shipments) {
        return shipments.stream()
                .map(s -> {
                    ShipmentDto dto = modelMapper.map(s, ShipmentDto.class);
                    String status = shipmentService.getShipmentHistory(dto.getUniqueID())
                            .getFirst()
                            .getStatus()
                            .toString();
                    dto.setStatus(status);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private String render(Model model, String contentFragment) {
        model.addAttribute("content", contentFragment);
        return "layouts/app";
    }

    @GetMapping("/shipments/all")
    public String viewAllForEmployee(Authentication auth, Model model) {
        List<Shipment> shipments = shipmentService.getAllShipmentsForEmployee(auth);
        model.addAttribute("shipments", mapAndSetStatus(shipments));
        model.addAttribute("title", "Employee Shipments");
        return render(model, "shipments/employee");
    }

    @GetMapping("/client/registerShipment")
    public String showRegisterForm(Model model, Authentication auth) throws ResourceNotFound {
        RegisterShipmentForm form = new RegisterShipmentForm();
        form.setCreateNewAddress(false);
        form.setDeliverToOffice(true);

        ClientDto client = clientService.getClientByEmail(auth.getName());

        if (client.getAddresses() != null && !client.getAddresses().isEmpty()) {
            form.setAddresses(client.getAddresses()); // full list for the view

            AddressDto first = client.getAddresses().get(0);
            if (first.getId() != null) {
                form.setSelectedSenderAddressId(first.getId());
            } else {
                form.setSenderAddress(first); // fallback if DTO doesn't carry id
            }
        }

        List<UserDtoSanitized> allUsers = this.userService.getAllUsers().stream()
                .filter(user -> user.getRole() == Role.CLIENT)
                .collect(Collectors.toList());
        model.addAttribute("users", allUsers);

        model.addAttribute("title", "Register Shipment");
        model.addAttribute("form", form);
        model.addAttribute("offices", officeService.getAll());
        return render(model, "client/registerShipment");
    }

    @PostMapping("/client/registerShipment")
    public String submitRegisterForm(@ModelAttribute RegisterShipmentForm form,
                                     Authentication auth) throws ResourceNotFound {
        ClientDto me = clientService.getClientByEmail(auth.getName());
        ShipmentDto dto = new ShipmentDto();
        dto.setWeight(form.getWeight());
        dto.setSenderClient(me);

        if (form.isCreateNewAddress()) {
            AddressDto address = clientService.addAddress(me.getId(), addressService.create(form.getSenderAddress()));
            dto.setSenderAddress(address);
        } else {
            if (form.getSelectedSenderAddressId() != null) {
                AddressDto selected = me.getAddresses().stream()
                        .filter(a -> a.getId().equals(form.getSelectedSenderAddressId()))
                        .findFirst().get();
                dto.setSenderAddress(selected);
            }
        }

        ClientDto recipient = clientService.getClientById(form.getRecipientClientId());
        dto.setRecipientClient(recipient);

        if (form.isDeliverToOffice()) {
            OfficeDto office = officeService.getById(form.getOfficeId());
            dto.setReceivingOffice(office);
        } else {
            dto.setRecipientAddress(form.getRecipientAddress());
        }

        dto.setPrice(form.getPrice());

        shipmentService.registerShipmentByClient(dto, auth);
        return "redirect:/shipments/client";
    }

    @GetMapping("/employee/registerShipment")
    public String showEmployeeRegisterForm(Model model, Authentication auth) throws ResourceNotFound {
        EmployeeRegisterShipmentForm form = new EmployeeRegisterShipmentForm();
        form.setDeliverToOffice(true);

        List<UserDtoSanitized> allUsers = this.userService.getAllUsers().stream()
                .filter(user -> user.getRole() == Role.CLIENT)
                .collect(Collectors.toList());
        model.addAttribute("users", allUsers);

        model.addAttribute("title", "Register Shipment");
        model.addAttribute("form", form);
        model.addAttribute("offices", officeService.getAll());
        return render(model, "employee/registerShipment");
    }

    @PostMapping("/employee/registerShipment")
    public String submitEmployeeRegisterForm(@ModelAttribute EmployeeRegisterShipmentForm form,
                                             Authentication auth) throws ResourceNotFound {
        ShipmentDto dto = new ShipmentDto();
        dto.setWeight(form.getWeight());

        ClientDto sender = clientService.getClientById(form.getSenderClientId());
        dto.setSenderClient(sender);

        OfficeDto sendingOffice = officeService.getById(form.getSendingOfficeId());
        dto.setSendingOffice(sendingOffice);
        dto.setSenderAddress(sendingOffice.getAddressDto());

        ClientDto recipient = clientService.getClientById(form.getRecipientClientId());
        dto.setRecipientClient(recipient);

        if (form.isDeliverToOffice()) {
            OfficeDto office = officeService.getById(form.getOfficeId());
            dto.setReceivingOffice(office);
        } else {
            dto.setRecipientAddress(form.getRecipientAddress());
        }

        dto.setPrice(form.getPrice());

        shipmentService.registerShipmentByEmployee(dto, auth);
        return "redirect:/shipments/all";
    }



    @GetMapping("/api/shipments/price")
    @ResponseBody
    public Map<String, Double> apiPrice(@RequestParam double weight,
                                        @RequestParam boolean deliverToOffice) {
        return Map.of("price", shipmentService.calculatePrice(weight, deliverToOffice));
    }

    @GetMapping("/shipments/client")
    public String viewClientShipments(Model model, Authentication auth) {
        List<Shipment> shipments = shipmentService.getShipmentsForClient(auth);
        model.addAttribute("shipments", mapAndSetStatus(shipments));
        model.addAttribute("title", "My Shipments");
        return render(model, "shipments/client");
    }

    @GetMapping("/api/shipments/employee/{id}")
    @ResponseBody
    public List<ShipmentDto> apiByEmployee(@PathVariable Integer id) {
        return shipmentService.getShipmentsByEmployeeId(id).stream()
                .map(s -> modelMapper.map(s, ShipmentDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/shipments/undelivered")
    @ResponseBody
    public List<ShipmentDto> apiUndelivered() {
        return shipmentService.getUndeliveredShipments().stream()
                .map(s -> modelMapper.map(s, ShipmentDto.class))
                .collect(Collectors.toList());
    }

    @PatchMapping("/api/shipments/status/{id}")
    @ResponseBody
    public ResponseEntity<ShipmentDto> apiUpdateStatus(@PathVariable Integer id,
                                                       @RequestBody Status newStatus) {
        try {
            Shipment updated = shipmentService.updateShipmentStatus(id, newStatus);
            return ResponseEntity.ok(modelMapper.map(updated, ShipmentDto.class));
        } catch (ShipmentNotFound e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/shipments/{id}")
    @ResponseBody
    public ResponseEntity<Void> apiDelete(@PathVariable Integer id) {
        try {
            shipmentService.deleteShipment(id);
            return ResponseEntity.noContent().build();
        } catch (ShipmentNotFound e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/shipments/statistics")
    @ResponseBody
    public Map<String, Object> apiStats() {
        return shipmentService.getShipmentStatistics();
    }
}
