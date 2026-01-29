package nbu.team11.services;
import nbu.team11.entities.Client;
import nbu.team11.entities.Address;
import nbu.team11.entities.Office;
import nbu.team11.dtos.ShipmentDto;
import nbu.team11.entities.Shipment;
import nbu.team11.entities.ShipmentStatus;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.Role;
import nbu.team11.entities.enums.Status;
import nbu.team11.repositories.*;
import nbu.team11.services.contracts.IShipmentService;
import nbu.team11.services.exceptions.ShipmentNotFound;
import nbu.team11.services.exceptions.UnauthorizedAccess;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

import java.math.BigDecimal;

/**
 * Service implementation for managing shipments.
 * Provides business logic for shipment registration, updates, and retrieval.
 */
@Service
public class ShipmentService implements IShipmentService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentStatusRepository shipmentStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private OfficeRepository officeRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

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

    public Shipment registerShipmentByClient(ShipmentDto dto, Authentication authentication) {

        if (dto.getWeight() <= 0) throw new IllegalArgumentException("Weight must be positive");
        if (dto.getSenderAddress() == null) throw new IllegalArgumentException("Sender address must be provided");
        if (dto.getReceivingOffice() == null && dto.getRecipientAddress() == null)
            throw new IllegalArgumentException("Recipient address must be provided for non-office delivery");

        Client sender = clientRepository.findById(dto.getSenderClient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Sender client not found"));

        Client recipient = clientRepository.findById(dto.getRecipientClient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient client not found"));

        Address recipientAddress = (dto.getRecipientAddress() == null) ? null
                : addressRepository.findById(dto.getRecipientAddress().getId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient address not found"));

        Office receivingOffice = (dto.getReceivingOffice() == null) ? null
                : officeRepository.findById(dto.getReceivingOffice().getId())
                .orElseThrow(() -> new IllegalArgumentException("Office not found"));


        Shipment shipment = new Shipment();
        if (dto.getReceivingOffice() != null){
            shipment.setDeliverToOffice(true);
            shipment.setReceivingOffice(receivingOffice);
        }
        else{
            shipment.setDeliverToOffice(false);
            shipment.setRecipientAddress(recipientAddress);
        }

        //If sender address present
        if(dto.getSenderAddress() != null){
            Address senderAddress = addressRepository.findById(dto.getSenderAddress().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Sender address not found"));
            shipment.setSenderAddress(senderAddress);
            shipment.setTakeFromClientAddress(true);
        }
        else{
            shipment.setTakeFromClientAddress(false);
        }

        shipment.setSenderClient(sender);
        shipment.setRecipientClient(recipient);
        shipment.setWeight(dto.getWeight());
        shipment.setPrice(dto.getPrice());
        shipment.setPayed(false);
        shipment.setUniqueId(UUID.randomUUID().toString());

        Shipment saved = shipmentRepository.save(shipment);

        ShipmentStatus initialStatus = new ShipmentStatus();
        initialStatus.setShipment(saved);
        initialStatus.setStatus(Status.SUBMITTED);
        shipmentStatusRepository.save(initialStatus);

        return saved;
    }

    public Shipment registerShipmentByEmployee(ShipmentDto dto, Authentication authentication) {

        if (dto.getWeight() <= 0) throw new IllegalArgumentException("Weight must be positive");
        if (dto.getSenderAddress() == null) throw new IllegalArgumentException("Sender address must be provided");
        if (dto.getReceivingOffice() == null && dto.getRecipientAddress() == null)
            throw new IllegalArgumentException("Recipient address must be provided for non-office delivery");

        Client sender = clientRepository.findById(dto.getSenderClient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Sender client not found"));

        Client recipient = clientRepository.findById(dto.getRecipientClient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient client not found"));

        Address recipientAddress = (dto.getRecipientAddress() == null) ? null
                : addressRepository.findById(dto.getRecipientAddress().getId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient address not found"));

        Office receivingOffice = (dto.getReceivingOffice() == null) ? null
                : officeRepository.findById(dto.getReceivingOffice().getId())
                .orElseThrow(() -> new IllegalArgumentException("Office not found"));

        Address senderAddress = (dto.getSenderAddress() == null) ? null
                : addressRepository.findById(dto.getSenderAddress().getId())
                .orElseThrow(() -> new IllegalArgumentException("Sender address not found"));

        Office sendingOffice = (dto.getSendingOffice() == null) ? null
                : officeRepository.findById(dto.getSendingOffice().getId())
                .orElseThrow(() -> new IllegalArgumentException("Sending office not found"));

        // Get employee from authentication
        Integer userId = getUserIdFromAuthentication(authentication);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        nbu.team11.entities.Employee employee = employeeRepository.findByUserId(userId);
        if (employee == null) {
            throw new IllegalArgumentException("Employee not found for user");
        }

        Shipment shipment = new Shipment();
        shipment.setEmployee(employee);

        if (dto.getReceivingOffice() != null){
            shipment.setDeliverToOffice(true);
            shipment.setReceivingOffice(receivingOffice);
        }
        else{
            shipment.setDeliverToOffice(false);
            shipment.setRecipientAddress(recipientAddress);
        }

        if(dto.getSenderAddress() != null){
            shipment.setSenderAddress(senderAddress);
            shipment.setTakeFromClientAddress(true);
        }
        else{
            shipment.setTakeFromClientAddress(false);
        }

        if (sendingOffice != null) {
            shipment.setSendingOffice(sendingOffice);
        }

        shipment.setSenderClient(sender);
        shipment.setRecipientClient(recipient);
        shipment.setWeight(dto.getWeight());
        shipment.setPrice(dto.getPrice());
        shipment.setPayed(false);
        shipment.setUniqueId(UUID.randomUUID().toString());

        Shipment saved = shipmentRepository.save(shipment);

        ShipmentStatus initialStatus = new ShipmentStatus();
        initialStatus.setShipment(saved);
        initialStatus.setStatus(Status.SUBMITTED);
        shipmentStatusRepository.save(initialStatus);

        return saved;
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
