
package nbu.team11.controllers;

import nbu.team11.dtos.ShipmentDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.entities.Shipment;
import nbu.team11.entities.ShipmentStatus;
import nbu.team11.entities.enums.Status;
import nbu.team11.services.ShipmentService;
import nbu.team11.services.exceptions.ResourceNotFound;
import nbu.team11.services.exceptions.ShipmentNotFound;
import nbu.team11.services.exceptions.UnauthorizedAccess;
import org.modelmapper.*;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for managing shipments in the system.
 * Provides endpoints for employees and clients to interact with shipment data.
 */
@Controller
@RequestMapping("/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ModelMapper modelMapper;

    private String withAppLayout(Model model) {
        return "layouts/app";
    }

    /**
     * Retrieves all shipments accessible to employees.
     *
     * @param authentication The authentication object containing user details.
     * @return List of all shipments as DTOs.
     */
    @GetMapping("/all")
    public ResponseEntity<List<ShipmentDto>> getAllShipments(Authentication authentication) {
        List<Shipment> shipments = shipmentService.getAllShipmentsForEmployee(authentication);
        List<ShipmentDto> shipmentDto = shipments.stream()
                .map(shipment -> modelMapper.map(shipment, ShipmentDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(shipmentDto);
    }

    /**
     * Retrieves all shipments registered by a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @return List of shipments registered by the employee as DTOs.
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ShipmentDto>> getShipmentsByEmployee(@PathVariable Integer employeeId) {
        List<Shipment> shipments = shipmentService.getShipmentsByEmployeeId(employeeId);
        List<ShipmentDto> shipmentDto = shipments.stream()
                .map(shipment -> modelMapper.map(shipment, ShipmentDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(shipmentDto);
    }

    /**
     * Retrieves all shipments that have not been delivered.
     *
     * @return List of undelivered shipments as DTOs.
     */
    @GetMapping("/undelivered")
    public ResponseEntity<List<ShipmentDto>> getUndeliveredShipments() {
        List<Shipment> shipments = shipmentService.getUndeliveredShipments();
        List<ShipmentDto> shipmentDtos = shipments.stream()
                .map(shipment -> modelMapper.map(shipment, ShipmentDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(shipmentDtos);
    }

    /**
     * Registers a new shipment in the system.
     *
     * @param shipmentDto    The DTO containing shipment details.
     * @param authentication The authentication object containing user details.
     * @return The registered shipment as a DTO.
     */
    @PostMapping("/register")
    public ResponseEntity<ShipmentDto> registerShipment(@RequestBody ShipmentDto shipmentDto,
            Authentication authentication) {
        try {
            Shipment shipment = modelMapper.map(shipmentDto, Shipment.class);
            Shipment registeredShipment = shipmentService.registerShipment(shipment, authentication);
            ShipmentDto registeredShipmentDto = modelMapper.map(registeredShipment, ShipmentDto.class);
            return ResponseEntity.ok(registeredShipmentDto);
        } catch (UnauthorizedAccess e) {
            return ResponseEntity.status(403).build();
        } catch (ShipmentNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves shipments sent or received by the authenticated client.
     *
     * @param authentication The authentication object containing user details.
     * @return List of shipments as DTOs.
     */
    @GetMapping("/client")
    public String getShipmentsByClient(Model model, Authentication authentication) {
        List<Shipment> shipments = shipmentService.getShipmentsForClient(authentication);
        List<ShipmentDto> shipmentDto = shipments.stream()
                .map(shipment -> modelMapper.map(shipment, ShipmentDto.class))
                .collect(Collectors.toList());

        for (int i = 0; i < shipmentDto.size(); i++) {
            ShipmentDto shipment = (ShipmentDto) shipmentDto.get(i);
            String status = shipmentService.getShipmentHistory(shipment.getId()).getFirst().getStatus().toString();
            shipment.setStatus(status);
        }
        model.addAttribute("shipments", shipmentDto);
        model.addAttribute("title", "client");
        model.addAttribute("content", "shipments/client");
        return withAppLayout(model);
    }

    /**
     * Updates the status of a specific shipment.
     *
     * @param shipmentId The ID of the shipment.
     * @param newStatus  The new status to set.
     * @return The updated shipment as a DTO.
     */
    @PatchMapping("/status/{shipmentId}")
    public ResponseEntity<ShipmentDto> updateShipmentStatus(@PathVariable Integer shipmentId,
            @RequestBody Status newStatus) {
        try {
            Shipment updatedShipment = shipmentService.updateShipmentStatus(shipmentId, newStatus);
            ShipmentDto updatedShipmentDto = modelMapper.map(updatedShipment, ShipmentDto.class);
            return ResponseEntity.ok(updatedShipmentDto);
        } catch (ShipmentNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves the history of statuses for a specific shipment.
     *
     * @param shipmentId The ID of the shipment.
     * @return List of shipment statuses.
     */
    @GetMapping("/history/{shipmentId}")
    public ResponseEntity<List<ShipmentStatus>> getShipmentHistory(@PathVariable Integer shipmentId) {
        try {
            List<ShipmentStatus> history = shipmentService.getShipmentHistory(shipmentId);
            return ResponseEntity.ok(history);
        } catch (ShipmentNotFound e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves statistics about all shipments in the system.
     *
     * @return A map containing total shipments, delivered shipments, total weight,
     *         and total revenue.
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getShipmentStatistics() {
        Map<String, Object> stats = shipmentService.getShipmentStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Retrieves shipments filtered by country and city.
     *
     * @param countryName The name of the country.
     * @param cityName    The name of the city.
     * @return List of shipments as DTOs.
     */
    @GetMapping("/by-location")
    public ResponseEntity<List<ShipmentDto>> getShipmentsByCountryAndCity(
            @RequestParam String countryName, @RequestParam String cityName) {
        List<Shipment> shipments = shipmentService.getShipmentsByCountryAndCity(countryName, cityName);
        List<ShipmentDto> shipmentDto = shipments.stream()
                .map(shipment -> modelMapper.map(shipment, ShipmentDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(shipmentDto);
    }

    /**
     * Deletes a shipment by its ID.
     *
     * @param shipmentId The ID of the shipment to delete.
     * @return A response with no content if the deletion is successful, or a 404
     *         status if the shipment is not found.
     */
    @DeleteMapping("/{shipmentId}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Integer shipmentId) {
        try {
            shipmentService.deleteShipment(shipmentId);
            return ResponseEntity.noContent().build();
        } catch (ShipmentNotFound e) {
            return ResponseEntity.notFound().build();
        }
    }

}
