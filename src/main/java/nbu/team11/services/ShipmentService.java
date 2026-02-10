package nbu.team11.services;
import nbu.team11.configurations.ShipmentPricingProperties;
import nbu.team11.entities.*;
import nbu.team11.dtos.*;
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
import java.math.RoundingMode;
import java.util.*;
import java.math.BigDecimal;

/**
 * Service implementation for managing shipments.
 * Provides business logic for shipment registration, updates, and retrieval.
 */
@Service
public class ShipmentService {
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

    private final ShipmentPricingProperties pricing;

    public ShipmentService(ShipmentPricingProperties pricing) {
        this.pricing = pricing;
    }

    public List<Shipment> getAllShipmentsForEmployee(Authentication authentication) {
        if (isEmployee(authentication)) {
            return shipmentRepository.findAll();
        }
        throw new UnauthorizedAccess("Access denied. Only employees can view all shipments.");
    }

    public BigDecimal calculatePrice(Double weight, boolean toOffice) {
        if (weight == null || weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }

        double base = toOffice ? pricing.getBaseToOffice() : pricing.getBaseToAddress();
        double total = base + (weight * pricing.getPerKilo());

        return BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
    }

    public List<Shipment> getShipmentsByEmployeeId(Integer employeeId) {
        return shipmentRepository.findAllByEmployeeId(employeeId);
    }

    public List<Shipment> getUndeliveredShipments() {
        return shipmentRepository.findUndeliveredShipments(Status.DELIVERED);
    }

    public Shipment markAsPaid(Integer shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFound("Shipment not found"));

        if (shipment.isPayed()) {
            throw new IllegalStateException("Shipment is already paid");
        }

        shipment.setPayed(true);
        return shipmentRepository.save(shipment);
    }

    public Shipment getShipmentById(Integer shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFound("Shipment not found with ID: " + shipmentId));
    }

    public boolean belongsToUser(Shipment shipment, Authentication authentication) {
        Integer userId = getUserIdFromAuthentication(authentication);

        if (isClient(authentication)) {
            return shipment.getSenderClient() != null
                    && shipment.getSenderClient().getUser().getId().equals(userId);
        }

        if (isEmployee(authentication)) {
            return true;
        }

        return false;
    }

    private void validateShipmentDto(ShipmentDto dto, boolean isEmployee) {
        if (dto.getWeight() <= 0)
            throw new IllegalArgumentException("Weight must be positive");

        if (!isEmployee && dto.getSenderAddress() == null)
            throw new IllegalArgumentException("Sender address must be provided");

        if (isEmployee && dto.getSenderAddress() == null && dto.getSendingOffice() == null)
            throw new IllegalArgumentException("Sender address or sending office must be provided");

        if (dto.getReceivingOffice() == null && dto.getRecipientAddress() == null)
            throw new IllegalArgumentException("Recipient address must be provided for non-office delivery");
    }

    private Client getClient(Integer id, String role) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(role + " client not found"));
    }

    private Address getAddress(AddressDto dto, String label) {
        if (dto == null) return null;
        return addressRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException(label + " address not found"));
    }

    private Office getOffice(OfficeDto dto, String label) {
        if (dto == null) return null;
        return officeRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException(label + " office not found"));
    }

    private Shipment buildBaseShipment(
            ShipmentDto dto,
            Client sender,
            Client recipient,
            Address recipientAddress,
            Office receivingOffice
    ) {
        Shipment shipment = new Shipment();

        if (receivingOffice != null) {
            shipment.setDeliverToOffice(true);
            shipment.setReceivingOffice(receivingOffice);
        } else {
            shipment.setDeliverToOffice(false);
            shipment.setRecipientAddress(recipientAddress);
        }

        shipment.setSenderClient(sender);
        shipment.setRecipientClient(recipient);
        shipment.setWeight(dto.getWeight());
        shipment.setPrice(dto.getPrice());
        shipment.setPayed(false);
        shipment.setUniqueId(UUID.randomUUID().toString());

        return shipment;
    }

    private void createInitialStatus(Shipment shipment) {
        ShipmentStatus status = new ShipmentStatus();
        status.setShipment(shipment);
        status.setStatus(Status.SUBMITTED);
        shipmentStatusRepository.save(status);
    }

    public Shipment registerShipmentByClient(ShipmentDto dto, Authentication auth) {

        validateShipmentDto(dto, false);

        Client sender = getClient(dto.getSenderClient().getId(), "Sender");
        Client recipient = getClient(dto.getRecipientClient().getId(), "Recipient");

        Address recipientAddress = getAddress(dto.getRecipientAddress(), "Recipient");
        Office receivingOffice = getOffice(dto.getReceivingOffice(), "Receiving");

        Shipment shipment = buildBaseShipment(dto, sender, recipient, recipientAddress, receivingOffice);

        Address senderAddress = getAddress(dto.getSenderAddress(), "Sender");
        shipment.setSenderAddress(senderAddress);
        shipment.setTakeFromClientAddress(senderAddress != null);

        Shipment saved = shipmentRepository.save(shipment);
        createInitialStatus(saved);

        return saved;
    }

    public Shipment registerShipmentByEmployee(ShipmentDto dto, Authentication auth) {

        validateShipmentDto(dto, true);

        Client sender = getClient(dto.getSenderClient().getId(), "Sender");
        Client recipient = getClient(dto.getRecipientClient().getId(), "Recipient");

        Address recipientAddress = getAddress(dto.getRecipientAddress(), "Recipient");
        Office receivingOffice = getOffice(dto.getReceivingOffice(), "Receiving");

        Shipment shipment = buildBaseShipment(dto, sender, recipient, recipientAddress, receivingOffice);

        Address senderAddress = getAddress(dto.getSenderAddress(), "Sender");
        Office sendingOffice = getOffice(dto.getSendingOffice(), "Sending");

        shipment.setSenderAddress(senderAddress);
        shipment.setTakeFromClientAddress(senderAddress != null);
        shipment.setSendingOffice(sendingOffice);

        // employee
        Integer userId = getUserIdFromAuthentication(auth);
        Employee employee = employeeRepository.findByUserId(userId);
        if (employee == null) throw new IllegalArgumentException("Employee not found");
        shipment.setEmployee(employee);

        Shipment saved = shipmentRepository.save(shipment);
        createInitialStatus(saved);

        return saved;
    }

    public Shipment assignShipmentToEmployee(Integer shipmentId, Integer employeeId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFound("Shipment not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        shipment.setEmployee(employee);
        return shipmentRepository.save(shipment);
    }

    public List<ShipmentStatus> getShipmentHistoryOrdered(String uniqueId) {
        Shipment shipment = shipmentRepository.findByUniqueId(uniqueId);

        if (shipment == null) {
            throw new ShipmentNotFound("Shipment not found with tracking ID: " + uniqueId);
        }

        return shipmentStatusRepository.findByShipmentIdOrderByCreatedOnAsc(shipment.getId());
    }


    public ShipmentStatus getLatestShipmentStatus(String uniqueId) {
        Shipment shipment = shipmentRepository.findByUniqueId(uniqueId);

        return shipmentStatusRepository
                .findByShipmentIdOrderByCreatedOnDesc(shipment.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Shipment has no status"));
    }

    public List<Shipment> getShipmentsFromClient(Authentication authentication) {
        if (isClient(authentication)) {
            Integer userId = getUserIdFromAuthentication(authentication);

            return shipmentRepository.findAll().stream()
                    .filter(s -> s.getSenderClient().getUser().getId().equals(userId))
                    .toList();
        }

        throw new UnauthorizedAccess("Access denied. Only clients can view their shipments.");
    }

    public List<Shipment> getShipmentsToClient(Authentication authentication) {
        if (isClient(authentication)) {
            Integer userId = getUserIdFromAuthentication(authentication);

            return shipmentRepository.findAll().stream()
                    .filter(s -> s.getRecipientClient().getUser().getId().equals(userId))
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

    //REFACTOR - Move to helper
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
                .orElseThrow(() -> new ShipmentNotFound("Shipment not found"));

        if (!shipment.isPayed()) {
            throw new IllegalStateException("Shipment must be paid before changing status");
        }

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
