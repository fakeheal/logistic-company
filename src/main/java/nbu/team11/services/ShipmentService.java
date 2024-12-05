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

    public Shipment createShipmentWithStatus(Shipment shipment, ShipmentStatus shipmentStatus) {
        // First save the shipment
        Shipment savedShipment = shipmentRepository.save(shipment);

        // Then create and save the initial shipment status
        shipmentStatus.setShipment(savedShipment); // Setting the saved shipment to the status
        shipmentStatusRepository.save(shipmentStatus);

        return savedShipment;
    }


    // Get all Shipments
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    // Get a Shipment by ID
    public Optional<Shipment> getShipmentById(Integer id) {
        return shipmentRepository.findById(id);
    }

    // Update Shipment and its Status
    public Shipment updateShipment(Integer id, Shipment updatedShipment, ShipmentStatus updatedStatus) {
        if (shipmentRepository.existsById(id)) {
            updatedShipment.setId(id);
            Shipment updatedShipmentEntity = shipmentRepository.save(updatedShipment);

            // Update shipment status
            updatedStatus.setShipment(updatedShipmentEntity);
            shipmentStatusRepository.save(updatedStatus);

            return updatedShipmentEntity;
        }
        return null;
    }

    // Delete Shipment and its Status
    public void deleteByShipmentId(Integer id) {
        shipmentRepository.deleteById(id);
        shipmentStatusRepository.deleteByShipmentId(id);
    }

}








