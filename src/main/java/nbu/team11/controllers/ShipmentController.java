package nbu.team11.controllers;
import jakarta.validation.Valid;
import nbu.team11.controllers.forms.PaymentForm;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import nbu.team11.controllers.forms.EmployeeRegisterShipmentForm;
import nbu.team11.controllers.forms.RegisterShipmentForm;
import nbu.team11.dtos.*;
import nbu.team11.entities.Address;
import nbu.team11.entities.Shipment;
import nbu.team11.entities.ShipmentStatus;
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
import java.math.RoundingMode;
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
    private final EmployeeService employeeService;
    private final OfficeService officeService;
    private final ShipmentService shipmentService;
    private final AddressService addressService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public ShipmentController(ClientService clientService,
                              EmployeeService employeeService,
                              OfficeService officeService,
                              ShipmentService shipmentService,
                              ModelMapper modelMapper,
                              UserService userService,
                              AddressService addressService) {
        this.clientService = clientService;
        this.employeeService = employeeService;
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
                    String status = shipmentService
                            .getLatestShipmentStatus(dto.getUniqueID())
                            .getStatus()
                            .name();
                    dto.setStatus(status);
                    return dto;
                })
                .collect(Collectors.toList());
    }


    private String render(Model model, String contentFragment) {
        model.addAttribute("content", contentFragment);
        return "layouts/app";
    }

    @GetMapping("/track")
    public String trackShipment(@RequestParam(value = "shipmentId", required = false) String shipmentId,
                                Model model) {

        model.addAttribute("title", "Track Shipment");

        if (shipmentId != null && !shipmentId.isBlank()) {
            try {
                List<ShipmentStatusDto> statusDtos =
                        shipmentService.getShipmentHistoryOrdered(shipmentId)
                                .stream()
                                .map(status -> modelMapper.map(status, ShipmentStatusDto.class))
                                .toList();

                model.addAttribute("shipmentId", shipmentId);
                model.addAttribute("shipmentStatuses", statusDtos);

            } catch (ShipmentNotFound e) {
                model.addAttribute("shipmentId", shipmentId);
                model.addAttribute("error",
                        "Shipment with tracking number \"" + shipmentId + "\" was not found.");
            }
        }

        return render(model, "shipments/track");
    }

    @GetMapping("/shipments/all")
    public String viewAllForEmployee(Authentication auth, Model model,
                                     @RequestParam(required = false) String search,
                                     @RequestParam(required = false) Status status) {

        List<Shipment> shipments;
        if ((search != null && !search.isBlank()) || status != null) {
            shipments = shipmentService.searchShipmentsForEmployee(auth, search, status);
        } else {
            shipments = shipmentService.getAllShipmentsForEmployee(auth);
        }

        List<EmployeeDto> employeeDtos = employeeService.getAllEmployees()
                .stream()
                .map(emp -> modelMapper.map(emp, EmployeeDto.class))
                .toList();

        model.addAttribute("shipments", mapAndSetStatus(shipments));
        model.addAttribute("employees", employeeDtos);
        model.addAttribute("statuses", Status.values());
        model.addAttribute("title", "Employee Shipments");
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);

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

    @PostMapping("/shipments/{id}/assign")
    public String assignShipment(@PathVariable Integer id,
                                 @RequestParam Integer employeeId) {
        shipmentService.assignShipmentToEmployee(id, employeeId);
        return "redirect:/shipments/all";
    }

    @PostMapping("/shipments/{id}/status")
    public String changeStatus(@PathVariable Integer id,
                               @RequestParam Status status,
                               RedirectAttributes redirectAttributes) {
        try {
            shipmentService.updateShipmentStatus(id, status);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute(
                    "error", "Shipment must be paid before changing status.");
        }
        return "redirect:/shipments/all";
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
            AddressDto selected = recipient.getAddresses().stream()
                    .filter(a -> a.getId().equals(form.getSelectedRecipientAddressId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException("Recipient address not selected"));

            dto.setRecipientAddress(selected);
        }


        dto.setPrice(form.getPrice());

        Shipment shipment = shipmentService.registerShipmentByClient(dto, auth);
        return "redirect:/shipments/" + shipment.getId() + "/pay";
    }

    @GetMapping("/shipments/{id}/pay")
    public String showPaymentPage(@PathVariable Integer id,
                                  Model model,
                                  Authentication auth)
    {

        Shipment shipment = shipmentService.getShipmentById(id);

        if (!shipmentService.belongsToUser(shipment, auth)) {
            return "redirect:/access-denied";
        }

        if (shipment.isPayed()) {
            return "redirect:/shipments/client";
        }

        model.addAttribute("shipment", shipment);
        model.addAttribute("paymentForm", new PaymentForm());
        model.addAttribute("title", "Pay Shipment");

        return render(model, "payments/pay");
    }

    @PostMapping("/shipments/{id}/pay")
    public String processPayment(@PathVariable Integer id,
                                 @Valid @ModelAttribute("paymentForm") PaymentForm paymentForm,
                                 BindingResult bindingResult,
                                 Authentication auth,
                                 Model model)
    {
        Shipment shipment = shipmentService.getShipmentById(id);

        // Security check
        if (!shipmentService.belongsToUser(shipment, auth)) {
            return "redirect:/access-denied";
        }

        // Validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("shipment", shipment);
            return render(model, "payments/pay");
        }

        if (shipment.isPayed()) {
            bindingResult.reject(
                    "payment.alreadyPaid",
                    "Shipment is already paid."
            );
            model.addAttribute("shipment", shipment);
            return "payments/pay";
        }

        shipmentService.markAsPaid(id);
        shipmentService.updateShipmentStatus(id, Status.SUBMITTED_PAID);

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

        ClientDto recipient = clientService.getClientById(form.getRecipientClientId());
        dto.setRecipientClient(recipient);

        if (form.isDeliverToOffice()) {
            OfficeDto office = officeService.getById(form.getOfficeId());
            dto.setReceivingOffice(office);
        } else {
            AddressDto selected = recipient.getAddresses().stream()
                    .filter(a -> a.getId().equals(form.getSelectedRecipientAddressId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException("Recipient address not selected"));

            dto.setRecipientAddress(selected);
        }

        dto.setPrice(form.getPrice());

        Shipment shipment = shipmentService.registerShipmentByEmployee(dto, auth);
        return "redirect:/employee/shipments/" + shipment.getId() + "/confirm-payment";
    }

    @GetMapping("/employee/shipments/{id}/confirm-payment")
    public String confirmPosPaymentPage(@PathVariable Integer id,
                                        Authentication auth,
                                        Model model) {

        Shipment shipment = shipmentService.getShipmentById(id);


        if (!auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.EMPLOYEE.name()))) {
            return "redirect:/access-denied";
        }

        model.addAttribute("shipment", shipment);
        model.addAttribute("title", "Confirm POS Payment");

        return render(model, "employee/confirmPayment");
    }

    @PostMapping("/employee/shipments/{id}/confirm-payment")
    public String confirmPosPayment(@PathVariable Integer id,
                                    Authentication auth,
                                    RedirectAttributes redirectAttributes) {

        Shipment shipment = shipmentService.getShipmentById(id);

        if (shipment.isPayed()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Shipment is already paid.");
            return "redirect:/shipments/all";
        }

        shipmentService.markAsPaid(id);
        shipmentService.updateShipmentStatus(id, Status.SUBMITTED_PAID);

        redirectAttributes.addFlashAttribute(
                "success", "Payment confirmed successfully.");

        return "redirect:/shipments/all";
    }


    @GetMapping("/api/shipments/price")
    @ResponseBody
    public Map<String, BigDecimal> apiPrice(@RequestParam double weight,
                                        @RequestParam boolean deliverToOffice) {
        BigDecimal price = shipmentService.calculatePrice(weight, deliverToOffice)
                .setScale(2, RoundingMode.HALF_UP);

        return Map.of(
                "price", price
        );
    }

    @GetMapping("/shipments/client")
    public String viewClientShipments(Model model, Authentication auth) {
        List<Shipment> sent = shipmentService.getShipmentsFromClient(auth);
        List<Shipment> received = shipmentService.getShipmentsToClient(auth);

        model.addAttribute("sentShipments", mapAndSetStatus(sent));
        model.addAttribute("receivedShipments", mapAndSetStatus(received));
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
