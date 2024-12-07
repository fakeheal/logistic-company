package nbu.team11.controllers;
import nbu.team11.entities.Shipment;
import nbu.team11.entities.ShipmentStatus;
import nbu.team11.entities.enums.Status;
import nbu.team11.repositories.ShipmentStatusRepository;
import nbu.team11.services.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/shipments")
public class ShipmentController{

    @Autowired
    private ShipmentService shipmentService;

    // Create Shipment with initial Shipment Status
    @PostMapping
    public ResponseEntity<Shipment> createShipmentWithStatus(
            @RequestBody Shipment shipment,
            @RequestParam("status") String initialStatus) {

        // Create the initial ShipmentStatus
        ShipmentStatus shipmentStatus = new ShipmentStatus();
        shipmentStatus.setStatus(Status.valueOf(initialStatus));
        Shipment savedShipment = shipmentService.createShipmentWithStatus(shipment, shipmentStatus);
        return new ResponseEntity<>(savedShipment, HttpStatus.CREATED);
    }


    // Get all Shipments
    @GetMapping
    public List<Shipment> getAllShipments() {
        return shipmentService.getAllShipments();
    }

    // Get Shipment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Integer id) {
        Optional<Shipment> shipment = shipmentService.getShipmentById(id);
        return shipment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update Shipment and its Status
    @PutMapping("/{id}")
    public ResponseEntity<Shipment> updateShipment(
            @PathVariable Integer id,
            @RequestBody Shipment shipment,
            @RequestParam("status") String status) {

        ShipmentStatus updatedStatus = new ShipmentStatus();
        updatedStatus.setStatus(Status.valueOf(status)); // Assuming Status is an Enum

        Shipment updatedShipment = shipmentService.updateShipment(id, shipment, updatedStatus);
        return updatedShipment != null ? ResponseEntity.ok(updatedShipment) : ResponseEntity.notFound().build();
    }

    // Delete Shipment and its Status
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Integer id) {
        try {
            shipmentService.deleteByShipmentId(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
