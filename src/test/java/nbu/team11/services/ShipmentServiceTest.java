package nbu.team11.services;

import nbu.team11.configurations.ShipmentPricingProperties;
import nbu.team11.dtos.AddressDto;
import nbu.team11.dtos.ClientDto;
import nbu.team11.dtos.OfficeDto;
import nbu.team11.dtos.ShipmentDto;
import nbu.team11.entities.*;
import nbu.team11.entities.enums.Role;
import nbu.team11.entities.enums.Status;
import nbu.team11.repositories.*;
import nbu.team11.services.exceptions.ShipmentNotFound;
import nbu.team11.services.exceptions.UnauthorizedAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShipmentService Tests")
class ShipmentServiceTest {

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ShipmentRepository shipmentRepository;
    @Mock
    private ShipmentStatusRepository shipmentStatusRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private OfficeRepository officeRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ShipmentPricingProperties pricing;

    @InjectMocks
    private ShipmentService shipmentService;

    private User testUser;
    private Client senderClient;
    private Client recipientClient;
    private Address senderAddress;
    private Address recipientAddress;
    private Office testOffice;
    private Employee testEmployee;
    private Shipment testShipment;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(shipmentService, "modelMapper", modelMapper);
        ReflectionTestUtils.setField(shipmentService, "shipmentRepository", shipmentRepository);
        ReflectionTestUtils.setField(shipmentService, "shipmentStatusRepository", shipmentStatusRepository);
        ReflectionTestUtils.setField(shipmentService, "userRepository", userRepository);
        ReflectionTestUtils.setField(shipmentService, "clientRepository", clientRepository);
        ReflectionTestUtils.setField(shipmentService, "addressRepository", addressRepository);
        ReflectionTestUtils.setField(shipmentService, "officeRepository", officeRepository);
        ReflectionTestUtils.setField(shipmentService, "employeeRepository", employeeRepository);

        testUser = new User(Role.EMPLOYEE, "emp@example.com", "pass", "Ivan", "Petrov");
        testUser.setId(1);

        User senderUser = new User(Role.CLIENT, "sender@example.com", "pass", "Petar", "Stoyanov");
        senderUser.setId(2);
        senderClient = new Client();
        senderClient.setId(1);
        senderClient.setUser(senderUser);

        User recipientUser = new User(Role.CLIENT, "recipient@example.com", "pass", "Anna", "Koleva");
        recipientUser.setId(3);
        recipientClient = new Client();
        recipientClient.setId(2);
        recipientClient.setUser(recipientUser);

        senderAddress = new Address();
        senderAddress.setId(1);
        senderAddress.setFormattedAddress("123 Sender St");
        senderAddress.setCoordinates("42.0,23.0");

        recipientAddress = new Address();
        recipientAddress.setId(2);
        recipientAddress.setFormattedAddress("456 Recipient St");
        recipientAddress.setCoordinates("42.1,23.1");

        testOffice = new Office();
        testOffice.setId(1);
        testOffice.setTitle("Sofia Central");

        testEmployee = new Employee(testUser, testOffice, nbu.team11.entities.enums.PositionType.COORDINATOR);
        testEmployee.setId(1);

        testShipment = new Shipment();
        testShipment.setId(1);
        testShipment.setSenderClient(senderClient);
        testShipment.setRecipientClient(recipientClient);
        testShipment.setSenderAddress(senderAddress);
        testShipment.setRecipientAddress(recipientAddress);
        testShipment.setWeight(2.5);
        testShipment.setPrice(new BigDecimal("15.00"));
        testShipment.setPayed(false);
        testShipment.setUniqueId("test-unique-id");
    }

    @SuppressWarnings("unchecked")
    private Authentication mockEmployeeAuth() {
        Authentication auth = mock(Authentication.class);
        Collection authorities = List.of(new SimpleGrantedAuthority(Role.EMPLOYEE.name()));
        lenient().when(auth.getAuthorities()).thenReturn(authorities);
        lenient().when(auth.getName()).thenReturn("emp@example.com");
        return auth;
    }

    @SuppressWarnings("unchecked")
    private Authentication mockClientAuth() {
        Authentication auth = mock(Authentication.class);
        Collection authorities = List.of(new SimpleGrantedAuthority(Role.CLIENT.name()));
        lenient().when(auth.getAuthorities()).thenReturn(authorities);
        lenient().when(auth.getName()).thenReturn("sender@example.com");
        return auth;
    }

    @Test
    @DisplayName("getAllShipmentsForEmployee() should return all shipments for employee")
    void getAllShipmentsForEmployee_ShouldReturnAll_WhenEmployee() {
        // Arrange
        Authentication auth = mockEmployeeAuth();
        when(shipmentRepository.findAll()).thenReturn(List.of(testShipment));

        // Act
        List<Shipment> result = shipmentService.getAllShipmentsForEmployee(auth);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getAllShipmentsForEmployee() should throw UnauthorizedAccess for non-employee")
    void getAllShipmentsForEmployee_ShouldThrow_WhenNotEmployee() {
        // Arrange
        Authentication auth = mockClientAuth();

        // Act & Assert
        assertThrows(UnauthorizedAccess.class,
                () -> shipmentService.getAllShipmentsForEmployee(auth));
    }

    @Test
    @DisplayName("searchShipmentsForEmployee() should return search results for employee")
    void searchShipmentsForEmployee_ShouldReturnResults_WhenEmployee() {
        // Arrange
        Authentication auth = mockEmployeeAuth();
        when(shipmentRepository.searchShipments("Petar", null)).thenReturn(List.of(testShipment));

        // Act
        List<Shipment> result = shipmentService.searchShipmentsForEmployee(auth, "Petar", null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("searchShipmentsForEmployee() should throw UnauthorizedAccess for non-employee")
    void searchShipmentsForEmployee_ShouldThrow_WhenNotEmployee() {
        // Arrange
        Authentication auth = mockClientAuth();

        // Act & Assert
        assertThrows(UnauthorizedAccess.class,
                () -> shipmentService.searchShipmentsForEmployee(auth, "test", null));
    }

    @Test
    @DisplayName("calculatePrice() should calculate price for office delivery")
    void calculatePrice_ShouldCalculateForOffice() {
        // Arrange
        when(pricing.getBaseToOffice()).thenReturn(5.0);
        when(pricing.getPerKilo()).thenReturn(2.0);

        // Act
        BigDecimal result = shipmentService.calculatePrice(3.0, true);

        // Assert
        assertEquals(new BigDecimal("11.00"), result);
    }

    @Test
    @DisplayName("calculatePrice() should calculate price for address delivery")
    void calculatePrice_ShouldCalculateForAddress() {
        // Arrange
        when(pricing.getBaseToAddress()).thenReturn(8.0);
        when(pricing.getPerKilo()).thenReturn(2.0);

        // Act
        BigDecimal result = shipmentService.calculatePrice(3.0, false);

        // Assert
        assertEquals(new BigDecimal("14.00"), result);
    }

    @Test
    @DisplayName("calculatePrice() should throw for null weight")
    void calculatePrice_ShouldThrow_WhenWeightNull() {
        assertThrows(IllegalArgumentException.class,
                () -> shipmentService.calculatePrice(null, true));
    }

    @Test
    @DisplayName("calculatePrice() should throw for zero weight")
    void calculatePrice_ShouldThrow_WhenWeightZero() {
        assertThrows(IllegalArgumentException.class,
                () -> shipmentService.calculatePrice(0.0, true));
    }

    @Test
    @DisplayName("calculatePrice() should throw for negative weight")
    void calculatePrice_ShouldThrow_WhenWeightNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> shipmentService.calculatePrice(-1.0, true));
    }

    @Test
    @DisplayName("getShipmentsByEmployeeId() should return shipments for employee")
    void getShipmentsByEmployeeId_ShouldReturnShipments() {
        // Arrange
        when(shipmentRepository.findAllByEmployeeId(1)).thenReturn(List.of(testShipment));

        // Act
        List<Shipment> result = shipmentService.getShipmentsByEmployeeId(1);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getUndeliveredShipments() should return undelivered shipments")
    void getUndeliveredShipments_ShouldReturnUndelivered() {
        // Arrange
        when(shipmentRepository.findUndeliveredShipments(Status.DELIVERED)).thenReturn(List.of(testShipment));

        // Act
        List<Shipment> result = shipmentService.getUndeliveredShipments();

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("markAsPaid() should mark shipment as paid")
    void markAsPaid_ShouldMarkAsPaid() {
        // Arrange
        when(shipmentRepository.findById(1)).thenReturn(Optional.of(testShipment));
        when(shipmentRepository.save(testShipment)).thenReturn(testShipment);

        // Act
        Shipment result = shipmentService.markAsPaid(1);

        // Assert
        assertTrue(result.isPayed());
        verify(shipmentRepository).save(testShipment);
    }

    @Test
    @DisplayName("markAsPaid() should throw when already paid")
    void markAsPaid_ShouldThrow_WhenAlreadyPaid() {
        // Arrange
        testShipment.setPayed(true);
        when(shipmentRepository.findById(1)).thenReturn(Optional.of(testShipment));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> shipmentService.markAsPaid(1));
        assertEquals("Shipment is already paid", exception.getMessage());
    }

    @Test
    @DisplayName("markAsPaid() should throw ShipmentNotFound when shipment does not exist")
    void markAsPaid_ShouldThrow_WhenNotFound() {
        // Arrange
        when(shipmentRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ShipmentNotFound.class,
                () -> shipmentService.markAsPaid(999));
    }

    @Test
    @DisplayName("getShipmentById() should return shipment when found")
    void getShipmentById_ShouldReturn_WhenFound() {
        // Arrange
        when(shipmentRepository.findById(1)).thenReturn(Optional.of(testShipment));

        // Act
        Shipment result = shipmentService.getShipmentById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    @DisplayName("getShipmentById() should throw ShipmentNotFound when not found")
    void getShipmentById_ShouldThrow_WhenNotFound() {
        // Arrange
        when(shipmentRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ShipmentNotFound.class,
                () -> shipmentService.getShipmentById(999));
    }

    @Test
    @DisplayName("belongsToUser() should return true for employee")
    void belongsToUser_ShouldReturnTrue_ForEmployee() {
        // Arrange
        Authentication auth = mockEmployeeAuth();
        when(userRepository.findByEmail("emp@example.com")).thenReturn(testUser);

        // Act
        boolean result = shipmentService.belongsToUser(testShipment, auth);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("belongsToUser() should return true when client is sender")
    void belongsToUser_ShouldReturnTrue_WhenClientIsSender() {
        // Arrange
        Authentication auth = mockClientAuth();
        User senderUser = senderClient.getUser();
        when(userRepository.findByEmail("sender@example.com")).thenReturn(senderUser);

        // Act
        boolean result = shipmentService.belongsToUser(testShipment, auth);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("belongsToUser() should return false when client is not sender")
    void belongsToUser_ShouldReturnFalse_WhenClientIsNotSender() {
        // Arrange
        Authentication auth = mockClientAuth();
        User otherUser = new User(Role.CLIENT, "other@example.com", "pass", "Other", "User");
        otherUser.setId(99);
        when(userRepository.findByEmail("sender@example.com")).thenReturn(otherUser);

        // Act
        boolean result = shipmentService.belongsToUser(testShipment, auth);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("registerShipmentByClient() should register shipment successfully")
    void registerShipmentByClient_ShouldRegister() {
        // Arrange
        Authentication auth = mockClientAuth();

        ClientDto senderDto = new ClientDto();
        senderDto.setId(1);
        ClientDto recipientDto = new ClientDto();
        recipientDto.setId(2);
        AddressDto senderAddrDto = new AddressDto();
        senderAddrDto.setId(1);
        OfficeDto officeDto = new OfficeDto();
        officeDto.setId(1);

        ShipmentDto dto = new ShipmentDto();
        dto.setWeight(2.5);
        dto.setPrice(new BigDecimal("15.00"));
        dto.setSenderClient(senderDto);
        dto.setRecipientClient(recipientDto);
        dto.setSenderAddress(senderAddrDto);
        dto.setReceivingOffice(officeDto);

        when(clientRepository.findById(1)).thenReturn(Optional.of(senderClient));
        when(clientRepository.findById(2)).thenReturn(Optional.of(recipientClient));
        when(addressRepository.findById(1)).thenReturn(Optional.of(senderAddress));
        when(officeRepository.findById(1)).thenReturn(Optional.of(testOffice));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(inv -> {
            Shipment s = inv.getArgument(0);
            s.setId(1);
            return s;
        });

        // Act
        Shipment result = shipmentService.registerShipmentByClient(dto, auth);

        // Assert
        assertNotNull(result);
        assertEquals(senderClient, result.getSenderClient());
        assertEquals(recipientClient, result.getRecipientClient());
        assertTrue(result.isDeliverToOffice());
        verify(shipmentStatusRepository).save(any(ShipmentStatus.class));
    }

    @Test
    @DisplayName("registerShipmentByClient() should throw when weight is zero")
    void registerShipmentByClient_ShouldThrow_WhenWeightZero() {
        // Arrange
        Authentication auth = mockClientAuth();
        ShipmentDto dto = new ShipmentDto();
        dto.setWeight(0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> shipmentService.registerShipmentByClient(dto, auth));
    }

    @Test
    @DisplayName("registerShipmentByClient() should throw when sender address is missing")
    void registerShipmentByClient_ShouldThrow_WhenNoSenderAddress() {
        // Arrange
        Authentication auth = mockClientAuth();
        OfficeDto officeDto = new OfficeDto();
        officeDto.setId(1);

        ShipmentDto dto = new ShipmentDto();
        dto.setWeight(2.5);
        dto.setSenderAddress(null);
        dto.setReceivingOffice(officeDto);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> shipmentService.registerShipmentByClient(dto, auth));
    }

    @Test
    @DisplayName("registerShipmentByEmployee() should register shipment successfully")
    void registerShipmentByEmployee_ShouldRegister() {
        // Arrange
        Authentication auth = mockEmployeeAuth();

        ClientDto senderDto = new ClientDto();
        senderDto.setId(1);
        ClientDto recipientDto = new ClientDto();
        recipientDto.setId(2);
        OfficeDto sendingOfficeDto = new OfficeDto();
        sendingOfficeDto.setId(1);
        OfficeDto receivingOfficeDto = new OfficeDto();
        receivingOfficeDto.setId(1);

        ShipmentDto dto = new ShipmentDto();
        dto.setWeight(3.0);
        dto.setPrice(new BigDecimal("20.00"));
        dto.setSenderClient(senderDto);
        dto.setRecipientClient(recipientDto);
        dto.setSendingOffice(sendingOfficeDto);
        dto.setReceivingOffice(receivingOfficeDto);

        when(clientRepository.findById(1)).thenReturn(Optional.of(senderClient));
        when(clientRepository.findById(2)).thenReturn(Optional.of(recipientClient));
        when(officeRepository.findById(1)).thenReturn(Optional.of(testOffice));
        when(userRepository.findByEmail("emp@example.com")).thenReturn(testUser);
        when(employeeRepository.findByUserId(1)).thenReturn(testEmployee);
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(inv -> {
            Shipment s = inv.getArgument(0);
            s.setId(2);
            return s;
        });

        // Act
        Shipment result = shipmentService.registerShipmentByEmployee(dto, auth);

        // Assert
        assertNotNull(result);
        assertEquals(testEmployee, result.getEmployee());
        verify(shipmentStatusRepository).save(any(ShipmentStatus.class));
    }

    @Test
    @DisplayName("registerShipmentByEmployee() should throw when employee not found")
    void registerShipmentByEmployee_ShouldThrow_WhenEmployeeNotFound() {
        // Arrange
        Authentication auth = mockEmployeeAuth();

        ClientDto senderDto = new ClientDto();
        senderDto.setId(1);
        ClientDto recipientDto = new ClientDto();
        recipientDto.setId(2);
        OfficeDto officeDto = new OfficeDto();
        officeDto.setId(1);

        ShipmentDto dto = new ShipmentDto();
        dto.setWeight(3.0);
        dto.setPrice(new BigDecimal("20.00"));
        dto.setSenderClient(senderDto);
        dto.setRecipientClient(recipientDto);
        dto.setSendingOffice(officeDto);
        dto.setReceivingOffice(officeDto);

        when(clientRepository.findById(1)).thenReturn(Optional.of(senderClient));
        when(clientRepository.findById(2)).thenReturn(Optional.of(recipientClient));
        when(officeRepository.findById(1)).thenReturn(Optional.of(testOffice));
        when(userRepository.findByEmail("emp@example.com")).thenReturn(testUser);
        when(employeeRepository.findByUserId(1)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> shipmentService.registerShipmentByEmployee(dto, auth));
    }

    @Test
    @DisplayName("assignShipmentToEmployee() should assign successfully")
    void assignShipmentToEmployee_ShouldAssign() {
        // Arrange
        when(shipmentRepository.findById(1)).thenReturn(Optional.of(testShipment));
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        when(shipmentRepository.save(testShipment)).thenReturn(testShipment);

        // Act
        Shipment result = shipmentService.assignShipmentToEmployee(1, 1);

        // Assert
        assertEquals(testEmployee, result.getEmployee());
    }

    @Test
    @DisplayName("assignShipmentToEmployee() should throw when shipment not found")
    void assignShipmentToEmployee_ShouldThrow_WhenShipmentNotFound() {
        // Arrange
        when(shipmentRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ShipmentNotFound.class,
                () -> shipmentService.assignShipmentToEmployee(999, 1));
    }

    @Test
    @DisplayName("assignShipmentToEmployee() should throw when employee not found")
    void assignShipmentToEmployee_ShouldThrow_WhenEmployeeNotFound() {
        // Arrange
        when(shipmentRepository.findById(1)).thenReturn(Optional.of(testShipment));
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> shipmentService.assignShipmentToEmployee(1, 999));
    }

    @Test
    @DisplayName("getShipmentHistoryOrdered() should return ordered history")
    void getShipmentHistoryOrdered_ShouldReturnHistory() {
        // Arrange
        ShipmentStatus status1 = new ShipmentStatus();
        status1.setStatus(Status.SUBMITTED);

        when(shipmentRepository.findByUniqueId("test-unique-id")).thenReturn(testShipment);
        when(shipmentStatusRepository.findByShipmentIdOrderByCreatedOnAsc(1)).thenReturn(List.of(status1));

        // Act
        List<ShipmentStatus> result = shipmentService.getShipmentHistoryOrdered("test-unique-id");

        // Assert
        assertEquals(1, result.size());
        assertEquals(Status.SUBMITTED, result.get(0).getStatus());
    }

    @Test
    @DisplayName("getShipmentHistoryOrdered() should throw when shipment not found")
    void getShipmentHistoryOrdered_ShouldThrow_WhenNotFound() {
        // Arrange
        when(shipmentRepository.findByUniqueId("unknown")).thenReturn(null);

        // Act & Assert
        assertThrows(ShipmentNotFound.class,
                () -> shipmentService.getShipmentHistoryOrdered("unknown"));
    }

    @Test
    @DisplayName("getLatestShipmentStatus() should return latest status")
    void getLatestShipmentStatus_ShouldReturnLatest() {
        // Arrange
        ShipmentStatus latest = new ShipmentStatus();
        latest.setStatus(Status.SHIPPED);

        when(shipmentRepository.findByUniqueId("test-unique-id")).thenReturn(testShipment);
        when(shipmentStatusRepository.findByShipmentIdOrderByCreatedOnDesc(1)).thenReturn(List.of(latest));

        // Act
        ShipmentStatus result = shipmentService.getLatestShipmentStatus("test-unique-id");

        // Assert
        assertEquals(Status.SHIPPED, result.getStatus());
    }

    @Test
    @DisplayName("getLatestShipmentStatus() should throw when no statuses exist")
    void getLatestShipmentStatus_ShouldThrow_WhenNoStatuses() {
        // Arrange
        when(shipmentRepository.findByUniqueId("test-unique-id")).thenReturn(testShipment);
        when(shipmentStatusRepository.findByShipmentIdOrderByCreatedOnDesc(1)).thenReturn(List.of());

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> shipmentService.getLatestShipmentStatus("test-unique-id"));
    }


    @Test
    @DisplayName("getShipmentsFromClient() should return sent shipments for client")
    void getShipmentsFromClient_ShouldReturnSent() {
        // Arrange
        Authentication auth = mockClientAuth();
        User senderUser = senderClient.getUser();
        when(userRepository.findByEmail("sender@example.com")).thenReturn(senderUser);
        when(shipmentRepository.findAll()).thenReturn(List.of(testShipment));

        // Act
        List<Shipment> result = shipmentService.getShipmentsFromClient(auth);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getShipmentsFromClient() should throw for non-client")
    void getShipmentsFromClient_ShouldThrow_WhenNotClient() {
        // Arrange
        Authentication auth = mockEmployeeAuth();

        // Act & Assert
        assertThrows(UnauthorizedAccess.class,
                () -> shipmentService.getShipmentsFromClient(auth));
    }


    @Test
    @DisplayName("getShipmentsToClient() should return received shipments for client")
    void getShipmentsToClient_ShouldReturnReceived() {
        // Arrange
        Authentication auth = mockClientAuth();
        User senderUser = senderClient.getUser();
        when(userRepository.findByEmail("sender@example.com")).thenReturn(senderUser);

        // Create shipment where our client is recipient
        Shipment shipmentToClient = new Shipment();
        shipmentToClient.setId(2);
        shipmentToClient.setRecipientClient(senderClient);
        shipmentToClient.setSenderClient(recipientClient);

        when(shipmentRepository.findAll()).thenReturn(List.of(testShipment, shipmentToClient));

        // Act
        List<Shipment> result = shipmentService.getShipmentsToClient(auth);

        // Assert
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getId());
    }

    @Test
    @DisplayName("getShipmentsToClient() should throw for non-client")
    void getShipmentsToClient_ShouldThrow_WhenNotClient() {
        // Arrange
        Authentication auth = mockEmployeeAuth();

        // Act & Assert
        assertThrows(UnauthorizedAccess.class,
                () -> shipmentService.getShipmentsToClient(auth));
    }

    @Test
    @DisplayName("updateShipmentStatus() should update status when paid")
    void updateShipmentStatus_ShouldUpdate_WhenPaid() {
        // Arrange
        testShipment.setPayed(true);
        when(shipmentRepository.findById(1)).thenReturn(Optional.of(testShipment));

        // Act
        Shipment result = shipmentService.updateShipmentStatus(1, Status.PROCESSED);

        // Assert
        verify(shipmentStatusRepository).save(any(ShipmentStatus.class));
    }

    @Test
    @DisplayName("updateShipmentStatus() should throw when not paid")
    void updateShipmentStatus_ShouldThrow_WhenNotPaid() {
        // Arrange
        testShipment.setPayed(false);
        when(shipmentRepository.findById(1)).thenReturn(Optional.of(testShipment));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> shipmentService.updateShipmentStatus(1, Status.PROCESSED));
        assertEquals("Shipment must be paid before changing status", exception.getMessage());
    }

    @Test
    @DisplayName("updateShipmentStatus() should throw ShipmentNotFound when not found")
    void updateShipmentStatus_ShouldThrow_WhenNotFound() {
        // Arrange
        when(shipmentRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ShipmentNotFound.class,
                () -> shipmentService.updateShipmentStatus(999, Status.PROCESSED));
    }

    @Test
    @DisplayName("updateShipmentStatus() should allow CANCELED when status is SUBMITTED")
    void updateShipmentStatus_ShouldAllowCanceled_WhenSubmitted() {
        // Arrange
        ShipmentStatus submittedStatus = new ShipmentStatus();
        submittedStatus.setStatus(Status.SUBMITTED);

        when(shipmentRepository.findById(1)).thenReturn(Optional.of(testShipment));
        when(shipmentRepository.findByUniqueId("test-unique-id")).thenReturn(testShipment);
        when(shipmentStatusRepository.findByShipmentIdOrderByCreatedOnDesc(1))
                .thenReturn(List.of(submittedStatus));

        // Act
        Shipment result = shipmentService.updateShipmentStatus(1, Status.CANCELED);

        // Assert
        verify(shipmentStatusRepository).save(any(ShipmentStatus.class));
    }

    @Test
    @DisplayName("updateShipmentStatus() should reject CANCELED when status is not SUBMITTED")
    void updateShipmentStatus_ShouldRejectCanceled_WhenNotSubmitted() {
        // Arrange
        ShipmentStatus paidStatus = new ShipmentStatus();
        paidStatus.setStatus(Status.SUBMITTED_PAID);

        when(shipmentRepository.findById(1)).thenReturn(Optional.of(testShipment));
        when(shipmentRepository.findByUniqueId("test-unique-id")).thenReturn(testShipment);
        when(shipmentStatusRepository.findByShipmentIdOrderByCreatedOnDesc(1))
                .thenReturn(List.of(paidStatus));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> shipmentService.updateShipmentStatus(1, Status.CANCELED));
        assertEquals("Shipment can only be canceled when in SUBMITTED status", exception.getMessage());
    }

    @Test
    @DisplayName("getShipmentHistory() should return history")
    void getShipmentHistory_ShouldReturnHistory() {
        // Arrange
        ShipmentStatus status = new ShipmentStatus();
        status.setStatus(Status.SUBMITTED);

        when(shipmentRepository.findByUniqueId("test-unique-id")).thenReturn(testShipment);
        when(shipmentStatusRepository.findByShipmentId(1)).thenReturn(List.of(status));

        // Act
        List<ShipmentStatus> result = shipmentService.getShipmentHistory("test-unique-id");

        // Assert
        assertEquals(1, result.size());
    }


    @Test
    @DisplayName("getShipmentStatistics() should return statistics map")
    void getShipmentStatistics_ShouldReturnStats() {
        // Arrange
        ShipmentStatus deliveredStatus = new ShipmentStatus();
        deliveredStatus.setStatus(Status.DELIVERED);
        ShipmentStatus submittedStatus = new ShipmentStatus();
        submittedStatus.setStatus(Status.SUBMITTED);

        when(shipmentRepository.count()).thenReturn(2L);
        when(shipmentStatusRepository.findAll()).thenReturn(List.of(deliveredStatus, submittedStatus));
        when(shipmentRepository.findAll()).thenReturn(List.of(testShipment));

        // Act
        Map<String, Object> result = shipmentService.getShipmentStatistics();

        // Assert
        assertEquals(2L, result.get("totalShipments"));
        assertEquals(1L, result.get("deliveredShipments"));
        assertEquals(2.5, result.get("totalWeight"));
        assertEquals(new BigDecimal("15.00"), result.get("totalRevenue"));
    }


    @Test
    @DisplayName("getAllShipments() should return all shipments")
    void getAllShipments_ShouldReturnAll() {
        // Arrange
        when(shipmentRepository.findAll()).thenReturn(List.of(testShipment));

        // Act
        List<Shipment> result = shipmentService.getAllShipments();

        // Assert
        assertEquals(1, result.size());
    }


    @Test
    @DisplayName("updateShipment() should update when shipment exists")
    void updateShipment_ShouldUpdate_WhenExists() {
        // Arrange
        Shipment updated = new Shipment();
        ShipmentStatus status = new ShipmentStatus();

        when(shipmentRepository.existsById(1)).thenReturn(true);
        when(shipmentRepository.save(updated)).thenReturn(updated);

        // Act
        Shipment result = shipmentService.updateShipment(1, updated, status);

        // Assert
        assertNotNull(result);
        verify(shipmentStatusRepository).save(status);
    }

    @Test
    @DisplayName("updateShipment() should return null when shipment does not exist")
    void updateShipment_ShouldReturnNull_WhenNotExists() {
        // Arrange
        when(shipmentRepository.existsById(999)).thenReturn(false);

        // Act
        Shipment result = shipmentService.updateShipment(999, new Shipment(), new ShipmentStatus());

        // Assert
        assertNull(result);
    }


    @Test
    @DisplayName("deleteShipment() should delete shipment successfully")
    void deleteShipment_ShouldDelete() {
        // Arrange
        when(shipmentRepository.findById(1)).thenReturn(Optional.of(testShipment));

        // Act
        shipmentService.deleteShipment(1);

        // Assert
        verify(shipmentRepository).delete(testShipment);
    }

    @Test
    @DisplayName("deleteShipment() should throw ShipmentNotFound when not found")
    void deleteShipment_ShouldThrow_WhenNotFound() {
        // Arrange
        when(shipmentRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ShipmentNotFound.class,
                () -> shipmentService.deleteShipment(999));
    }
}
