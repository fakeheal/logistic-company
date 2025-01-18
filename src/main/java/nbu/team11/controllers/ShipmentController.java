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
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    // Create a new shipment with status
    @PostMapping
    public ResponseEntity<Shipment> createShipment(@RequestBody Shipment shipment, @RequestParam("status") String initialStatus) {
        ShipmentStatus shipmentStatus = new ShipmentStatus();
        shipmentStatus.setStatus(Status.valueOf(initialStatus));
        Shipment savedShipment = shipmentService.createShipmentWithStatus(shipment, shipmentStatus);
        return new ResponseEntity<>(savedShipment, HttpStatus.CREATED);
    }

    // Get all shipments
    @GetMapping
    public List<Shipment> getAllShipments() {
        return shipmentService.getAllShipments();
    }

    // Get a shipment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Integer id) {
        Optional<Shipment> shipment = shipmentService.getShipmentById(id);
        return shipment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update shipment and its status
    @PutMapping("/{id}")
    public ResponseEntity<Shipment> updateShipment(
            @PathVariable Integer id,
            @RequestBody Shipment updatedShipment,
            @RequestParam("status") String status) {

        ShipmentStatus updatedStatus = new ShipmentStatus();
        updatedStatus.setStatus(Status.valueOf(status));
        Shipment updatedShipmentEntity = shipmentService.updateShipment(id, updatedShipment, updatedStatus);
        return updatedShipmentEntity != null ? ResponseEntity.ok(updatedShipmentEntity) : ResponseEntity.notFound().build();
    }

    // Delete a shipment and its status
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable Integer id) {
        try {
            shipmentService.deleteByShipmentId(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    public List<Shipment> getAllShipmentsOrderedByDate() {
        return shipmentService.getAllShipmentsOrderedByDate();
    }

    @GetMapping("/by-employee/{employeeId}")
    public List<Shipment> getShipmentsByEmployee(@PathVariable Integer employeeId) {
        return shipmentService.getShipmentsByEmployeeId(employeeId);
    }

    @GetMapping("/undelivered")
    public List<Shipment> getUndeliveredShipments() {
        return shipmentService.getUndeliveredShipments();
    }

}
