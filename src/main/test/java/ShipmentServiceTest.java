import nbu.team11.entities.*;
import nbu.team11.entities.enums.Role;
import nbu.team11.entities.enums.Status;
import nbu.team11.repositories.ShipmentRepository;
import nbu.team11.repositories.ShipmentStatusRepository;
import nbu.team11.repositories.UserRepository;
import nbu.team11.services.ShipmentService;
import nbu.team11.services.exceptions.ShipmentNotFound;
import nbu.team11.services.exceptions.UnauthorizedAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private ShipmentStatusRepository shipmentStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ShipmentService shipmentService;

    private Shipment shipment;
    private ShipmentStatus shipmentStatus;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Създаване на тестова пратка
        shipment = new Shipment();
        shipment.setId(1);
        shipment.setWeight(5.0);
        shipment.setPrice(BigDecimal.valueOf(20.0));

        shipmentStatus = new ShipmentStatus();
        shipmentStatus.setShipment(shipment);
        shipmentStatus.setStatus(Status.SUBMITTED);
    }

    @Test
    void testGetUndeliveredShipments() {
        when(shipmentRepository.findUndeliveredShipments(Status.DELIVERED)).thenReturn(List.of(shipment));

        List<Shipment> result = shipmentService.getUndeliveredShipments();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(shipment.getId(), result.get(0).getId());

        verify(shipmentRepository, times(1)).findUndeliveredShipments(Status.DELIVERED);
    }

    @Test
    void testUpdateShipmentStatus_WhenShipmentExists() {
        when(shipmentRepository.findById(1)).thenReturn(Optional.of(shipment));
        when(shipmentStatusRepository.save(any(ShipmentStatus.class))).thenReturn(shipmentStatus);

        Shipment result = shipmentService.updateShipmentStatus(1, Status.IN_TRANSIT);

        assertNotNull(result);
        verify(shipmentStatusRepository, times(1)).save(any(ShipmentStatus.class));
    }

    @Test
    void testUpdateShipmentStatus_WhenShipmentDoesNotExist() {
        when(shipmentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ShipmentNotFound.class, () -> shipmentService.updateShipmentStatus(1, Status.IN_TRANSIT));
    }

    @Test
    void testGetShipmentHistory_WithDifferentStatuses() {
        when(shipmentRepository.findById(1)).thenReturn(Optional.of(shipment));

        ShipmentStatus status1 = new ShipmentStatus();
        status1.setShipment(shipment);
        status1.setStatus(Status.SUBMITTED);

        ShipmentStatus status2 = new ShipmentStatus();
        status2.setShipment(shipment);
        status2.setStatus(Status.SHIPPED);

        ShipmentStatus status3 = new ShipmentStatus();
        status3.setShipment(shipment);
        status3.setStatus(Status.DELIVERED);

        when(shipmentStatusRepository.findByShipmentId(1)).thenReturn(List.of(status1, status2, status3));

        List<ShipmentStatus> history = shipmentService.getShipmentHistory(1);

        assertNotNull(history);
        assertEquals(3, history.size());
        assertEquals(Status.SUBMITTED, history.get(0).getStatus());
        assertEquals(Status.SHIPPED, history.get(1).getStatus());
        assertEquals(Status.DELIVERED, history.get(2).getStatus());

        verify(shipmentStatusRepository, times(1)).findByShipmentId(1);
    }


    @Test
    void testGetShipmentHistory_WhenShipmentDoesNotExist() {
        when(shipmentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ShipmentNotFound.class, () -> shipmentService.getShipmentHistory(1));
    }

    @Test
    void testDeleteShipment_WhenShipmentExists_ShouldDelete() {
        when(shipmentRepository.findById(1)).thenReturn(Optional.of(shipment));

        shipmentService.deleteShipment(1);

        verify(shipmentRepository, times(1)).delete(shipment);
    }
    @Test
    void testGetShipmentsByEmployeeId_ShouldReturnShipments() {
        when(shipmentRepository.findAllByEmployeeId(1)).thenReturn(List.of(shipment));

        List<Shipment> shipments = shipmentService.getShipmentsByEmployeeId(1);

        assertNotNull(shipments);
        assertEquals(1, shipments.size());
    }

    @Test
    void testGetUndeliveredShipments_ShouldReturnUndeliveredShipments() {
        when(shipmentRepository.findUndeliveredShipments(Status.DELIVERED)).thenReturn(List.of(shipment));

        List<Shipment> shipments = shipmentService.getUndeliveredShipments();

        assertNotNull(shipments);
        assertEquals(1, shipments.size());
    }

    @Test
    void testDeleteShipment_WhenShipmentDoesNotExist_ShouldThrowShipmentNotFound() {
        when(shipmentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ShipmentNotFound.class, () -> shipmentService.deleteShipment(1));
    }
    @Test
    void testGetShipmentStatistics_ShouldReturnCorrectStatistics() {
        when(shipmentRepository.count()).thenReturn(5L);
        when(shipmentRepository.findAll()).thenReturn(List.of(shipment));
        ShipmentStatus shipmentStatus = new ShipmentStatus();
        shipmentStatus.setShipment(shipment);
        shipmentStatus.setStatus(Status.DELIVERED);

        when(shipmentStatusRepository.findAll()).thenReturn(List.of(shipmentStatus));

        Map<String, Object> stats = shipmentService.getShipmentStatistics();

        assertEquals(5L, stats.get("totalShipments"));
        assertEquals(1L, stats.get("deliveredShipments"));
        assertEquals(5.0, stats.get("totalWeight"));
        assertEquals(BigDecimal.valueOf(20.0), stats.get("totalRevenue"));
    }
    @Test
    void testGetShipmentsForClient_WhenUserIsClient_ShouldReturnShipments() {
        User user = new User();
        user.setId(1);
        Client client = new Client();
        client.setUser(user);
        shipment.setSender(client);
        shipment.setRecipient(client);
        when(authentication.getName()).thenReturn("clientUser");
        when(userRepository.findByUsername("clientUser")).thenReturn(user);
        when(authentication.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("CLIENT")));

        when(shipmentRepository.findAll()).thenReturn(List.of(shipment));

        List<Shipment> result = shipmentService.getShipmentsForClient(authentication);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }



    @Test
    void testGetShipmentsByCountryAndCity_ShouldReturnFilteredShipments() {
        City city = new City();
        city.setName("Sofia");
        Country country = new Country();
        country.setName("Bulgaria");
        city.setCountry(country);

        Address senderAddress = new Address();
        senderAddress.setCity(city);
        shipment.setSenderAddress(senderAddress);

        when(shipmentRepository.findAll()).thenReturn(List.of(shipment));

        List<Shipment> result = shipmentService.getShipmentsByCountryAndCity("Bulgaria", "Sofia");

        assertNotNull(result);
        assertEquals(1, result.size());
    }
    @Test
    void testRegisterShipment_WhenUserIsEmployee_ShouldSucceed() {
        when(authentication.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("EMPLOYEE")));
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);
        Address senderAddress = new Address();
        senderAddress.setId(1);

        Address recipientAddress = new Address();
        recipientAddress.setId(2);

        shipment.setSenderAddress(senderAddress);
        shipment.setRecipientAddress(recipientAddress);

        Shipment result = shipmentService.registerShipment(shipment, authentication);

        assertNotNull(result);
        verify(shipmentRepository, times(1)).save(shipment);
    }


    @Test
    void testGetShipmentsForClient_WhenUserIsNotClient_ShouldThrowUnauthorizedAccess() {
        when(authentication.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("EMPLOYEE")));

        assertThrows(UnauthorizedAccess.class, () -> shipmentService.getShipmentsForClient(authentication));
    }

    @Test
    void testRegisterShipment_WhenUserIsNotEmployee_ShouldThrowUnauthorizedAccess() {
        when(authentication.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("CLIENT")));

        assertThrows(UnauthorizedAccess.class, () -> shipmentService.registerShipment(shipment, authentication));
    }

    @Test
    void testRegisterShipment_WhenInvalidWeight_ShouldThrowIllegalArgumentException() {
        shipment.setWeight(-5.0);
        when(authentication.getAuthorities()).thenAnswer(invocation -> List.of(new SimpleGrantedAuthority("EMPLOYEE")));

        assertThrows(IllegalArgumentException.class, () -> shipmentService.registerShipment(shipment, authentication));
    }

}

