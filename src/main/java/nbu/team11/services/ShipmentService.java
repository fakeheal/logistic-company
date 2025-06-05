package nbu.team11.services;

import nbu.team11.controllers.forms.RegisterShipmentForm;
import nbu.team11.entities.Shipment;
import nbu.team11.entities.ShipmentStatus;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.Role;
import nbu.team11.entities.enums.Status;
import nbu.team11.repositories.ShipmentRepository;
import nbu.team11.repositories.ShipmentStatusRepository;
import nbu.team11.repositories.UserRepository;
import nbu.team11.services.contracts.IShipmentService;
import nbu.team11.services.exceptions.ShipmentNotFound;
import nbu.team11.services.exceptions.UnauthorizedAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Service implementation for managing shipments.
 * Provides business logic for shipment registration, updates, and retrieval.
 */
@Service
public class ShipmentService implements IShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentStatusRepository shipmentStatusRepository;

    @Autowired
    private UserRepository userRepository;

    private static final double BASE_PRICE_TO_OFFICE = 5.0;
    private static final double BASE_PRICE_TO_ADDRESS = 10.0;
    private static final double PRICE_PER_KILO = 2.0;

    /**
     * Retrieves all shipments accessible to employees.
     *
     * @param authentication The authentication object containing user details.
     * @return List of all shipments.
     * @throws UnauthorizedAccess If the user is not an employee.
     */
    public List<Shipment> getAllShipmentsForEmployee(Authentication authentication) {
        if (isEmployee(authentication)) {
            return shipmentRepository.findAll();
        }
        throw new UnauthorizedAccess("Access denied. Only employees can view all shipments.");
    }

    @Override
    public double calculatePrice(Double weight, boolean toOffice) {
        double base = toOffice ? BASE_PRICE_TO_OFFICE : BASE_PRICE_TO_ADDRESS;
        return base + (weight * PRICE_PER_KILO);
    }

    public List<Shipment> getShipmentsByEmployeeId(Integer employeeId) {
        return shipmentRepository.findAllByEmployeeId(employeeId);
    }

    public List<Shipment> getUndeliveredShipments() {
        return shipmentRepository.findUndeliveredShipments(Status.DELIVERED);
    }

    public Shipment registerShipment(Shipment shipment, Authentication authentication) {
        if (!isEmployee(authentication)) {
            throw new UnauthorizedAccess("Only employees can register shipments.");
        }
        validateShipment(shipment);
        return shipmentRepository.save(shipment);
    }

    public List<Shipment> getShipmentsForClient(Authentication authentication) {
        if (isClient(authentication)) {
            Integer userId = getUserIdFromAuthentication(authentication);
            return shipmentRepository.findAll().stream()
                    .filter(s -> s.getSenderClient().getUser().getId().equals(userId)
                            || s.getRecipientClient().getUser().getId().equals(userId))
                    .toList();
        }
        throw new UnauthorizedAccess("Access denied. Only clients can view their shipments.");
    }

    private void validateShipment(Shipment shipment) {
        if (shipment.getWeight() <= 0) {
            throw new IllegalArgumentException("Weight must be a positive value.");
        }
        if (shipment.getSenderAddress() == null || shipment.getRecipientAddress() == null) {
            throw new IllegalArgumentException("Sender and recipient addresses must be provided.");
        }
    }

    private boolean isEmployee(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(Role.EMPLOYEE.name()));
    }

    private boolean isClient(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(Role.CLIENT.name()));
    }

    private Integer getUserIdFromAuthentication(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName());
        return user.getId();
    }


    public Shipment updateShipmentStatus(Integer shipmentId, Status newStatus) {

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFound("Shipment not found with ID: " + shipmentId));

        ShipmentStatus shipmentStatus = new ShipmentStatus();
        shipmentStatus.setShipment(shipment);
        shipmentStatus.setStatus(newStatus);
        shipmentStatusRepository.save(shipmentStatus);

        return shipment;
    }

    public List<ShipmentStatus> getShipmentHistory(String uniqueId) {
        Shipment shipment = shipmentRepository.findByUniqueId(uniqueId);
        return shipmentStatusRepository.findByShipmentId(shipment.getId());
    }

    public Map<String, Object> getShipmentStatistics() {
        long totalShipments = shipmentRepository.count();
        long deliveredShipments = shipmentStatusRepository.findAll().stream()
                .filter(shipmentStatus -> shipmentStatus.getStatus() == Status.DELIVERED)
                .count();
        double totalWeight = shipmentRepository.findAll().stream()
                .mapToDouble(Shipment::getWeight)
                .sum();

        BigDecimal totalRevenue = shipmentRepository.findAll().stream()
                .map(Shipment::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalShipments", totalShipments);
        stats.put("deliveredShipments", deliveredShipments);
        stats.put("totalWeight", totalWeight);
        stats.put("totalRevenue", totalRevenue);

        return stats;
    }

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public Optional<Shipment> getShipmentById(Integer id) {
        return shipmentRepository.findById(id);
    }

    public Shipment updateShipment(Integer id, Shipment updatedShipment, ShipmentStatus updatedStatus) {
        if (shipmentRepository.existsById(id)) {
            updatedShipment.setId(id);
            Shipment updatedShipmentEntity = shipmentRepository.save(updatedShipment);

            updatedStatus.setShipment(updatedShipmentEntity);
            shipmentStatusRepository.save(updatedStatus);

            return updatedShipmentEntity;
        }
        return null;
    }

    public void deleteShipment(Integer shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFound("Shipment not found with ID: " + shipmentId));
        shipmentRepository.delete(shipment);
    }

}
