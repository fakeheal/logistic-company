package nbu.team11.services.contracts;

import nbu.team11.dtos.ShipmentDto;
import nbu.team11.entities.Shipment;
import nbu.team11.entities.ShipmentStatus;
import nbu.team11.entities.enums.Status;
import nbu.team11.services.exceptions.ResourceNotFound;
import nbu.team11.services.exceptions.ShipmentNotFound;
import nbu.team11.services.exceptions.UnauthorizedAccess;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IShipmentService {

    List<Shipment> getAllShipmentsForEmployee(Authentication authentication) throws UnauthorizedAccess;

    List<Shipment> getShipmentsByEmployeeId(Integer employeeId);

    List<Shipment> getUndeliveredShipments();

    Shipment registerShipment(Shipment shipment, Authentication authentication) throws UnauthorizedAccess, ResourceNotFound;

    List<Shipment> getShipmentsForClient(Authentication authentication) throws UnauthorizedAccess;

    Shipment updateShipmentStatus(Integer shipmentId, Status newStatus) throws ShipmentNotFound;

    List<ShipmentStatus> getShipmentHistory(Integer shipmentId) throws ShipmentNotFound;
}
