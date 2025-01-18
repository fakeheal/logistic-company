package nbu.team11.services;
import nbu.team11.entities.Shipment;
import nbu.team11.entities.ShipmentStatus;
import nbu.team11.entities.enums.Status;
import nbu.team11.repositories.ShipmentRepository;
import nbu.team11.repositories.ShipmentStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import nbu.team11.controllers.ShipmentController;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentStatusRepository shipmentStatusRepository;

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public Optional<Shipment> getShipmentById(Integer id) {
        return shipmentRepository.findById(id);
    }

    public Shipment createShipmentWithStatus(Shipment shipment, ShipmentStatus shipmentStatus) {
        if (shipment.getWeight() <= 0) {
            throw new IllegalArgumentException("Weight must be a positive value.");
        }

        if (shipment.getSenderAddress() == null || shipment.getRecipientAddress() == null) {
            throw new IllegalArgumentException("Sender and receiver addresses must be provided.");
        }

        // Continue with saving the shipment and status
        Shipment savedShipment = shipmentRepository.save(shipment);
        shipmentStatus.setShipment(savedShipment);
        shipmentStatusRepository.save(shipmentStatus);
        return savedShipment;
    }

    public Shipment updateShipment(Integer id, Shipment updatedShipment, ShipmentStatus updatedStatus) {
        if (updatedShipment.getWeight() <= 0) {
            throw new IllegalArgumentException("Weight must be a positive value.");
        }
        if (updatedShipment.getSenderAddress() == null || updatedShipment.getRecipientAddress() == null) {
            throw new IllegalArgumentException("Sender and receiver addresses must be provided.");
        }
        updatedShipment.setId(id);
        Shipment updatedShipmentEntity = shipmentRepository.save(updatedShipment);

        updatedStatus.setShipment(updatedShipmentEntity);
        shipmentStatusRepository.save(updatedStatus);

        return updatedShipmentEntity;
    }


    // Delete Shipment and its Status
    public void deleteByShipmentId(Integer id) {
        shipmentRepository.deleteById(id);
        shipmentStatusRepository.deleteByShipmentId(id);
    }

    public List<Shipment> getAllShipmentsOrderedByDate() {
        return shipmentRepository.findAllByOrderByCreatedOnDesc();
    }
    public List<Shipment> getShipmentsByEmployeeId(Integer employeeId) {
        return shipmentRepository.findAllByEmployeeId(employeeId);
    }
    public List<Shipment> getUndeliveredShipments() {
        return shipmentRepository.findUndeliveredShipments(Status.DELIVERED);
    }


}







