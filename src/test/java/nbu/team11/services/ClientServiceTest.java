package nbu.team11.services;

import nbu.team11.dtos.AddressDto;
import nbu.team11.dtos.ClientDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.entities.Address;
import nbu.team11.entities.Client;
import nbu.team11.entities.Shipment;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.Role;
import nbu.team11.entities.enums.Status;
import nbu.team11.repositories.AddressRepository;
import nbu.team11.repositories.ClientRepository;
import nbu.team11.repositories.ShipmentRepository;
import nbu.team11.repositories.UserRepository;
import nbu.team11.services.exceptions.AddressLinkedToShipment;
import nbu.team11.services.exceptions.CannotDeleteClient;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService Tests")
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientService clientService;

    private User testUser;
    private Client testClient;
    private ClientDto testClientDto;

    @BeforeEach
    void setUp() {
        testUser = new User(Role.CLIENT, "client@example.com", "encodedPassword", "Petar", "Stoyanov");
        testUser.setId(1);

        testClient = new Client();
        testClient.setId(1);
        testClient.setUser(testUser);
        testClient.setPhoneNumber("0888123456");

        testClientDto = new ClientDto();
        testClientDto.setId(1);
        testClientDto.setUserId(1);
        testClientDto.setPhoneNumber("0888123456");
        testClientDto.setUserFullName("Petar Stoyanov");
    }

    @Test
    @DisplayName("createClient() should create client successfully")
    void createClient_ShouldCreateClientSuccessfully() throws ResourceNotFound {
        // Arrange
        ClientDto inputDto = new ClientDto();
        inputDto.setPhoneNumber("0888111222");

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);
        when(modelMapper.map(testClient, ClientDto.class)).thenReturn(testClientDto);

        // Act
        ClientDto result = clientService.createClient(inputDto, 1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("0888123456", result.getPhoneNumber());
    }

    @Test
    @DisplayName("createClient() should throw ResourceNotFound when user does not exist")
    void createClient_ShouldThrowResourceNotFound_WhenUserDoesNotExist() {
        // Arrange
        ClientDto inputDto = new ClientDto();
        inputDto.setPhoneNumber("0888111222");

        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFound.class,
                () -> clientService.createClient(inputDto, 999));
    }

    @Test
    @DisplayName("createClientWithGeneratedPassword() should create client with generated password")
    void createClientWithGeneratedPassword_ShouldCreateClient() throws EmailNotAvailable {
        // Arrange
        ClientDto inputClientDto = new ClientDto();
        inputClientDto.setPhoneNumber("0888111222");

        UserDto inputUserDto = new UserDto();
        inputUserDto.setFirstName("Anna");
        inputUserDto.setLastName("Koleva");
        inputUserDto.setEmail("anna@example.com");

        when(userRepository.findByEmail("anna@example.com")).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedGeneratedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(2);
            return saved;
        });
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);
        when(modelMapper.map(testClient, ClientDto.class)).thenReturn(testClientDto);

        // Act
        ClientDto result = clientService.createClientWithGeneratedPassword(inputClientDto, inputUserDto);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        verify(clientRepository).save(any(Client.class));
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    @DisplayName("createClientWithGeneratedPassword() should throw EmailNotAvailable when email exists")
    void createClientWithGeneratedPassword_ShouldThrowEmailNotAvailable_WhenEmailExists() {
        // Arrange
        ClientDto inputClientDto = new ClientDto();
        UserDto inputUserDto = new UserDto();
        inputUserDto.setEmail("client@example.com");

        when(userRepository.findByEmail("client@example.com")).thenReturn(testUser);

        // Act & Assert
        assertThrows(EmailNotAvailable.class,
                () -> clientService.createClientWithGeneratedPassword(inputClientDto, inputUserDto));
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("getClientByUserId() should return ClientDto when client exists")
    void getClientByUserId_ShouldReturnClientDto_WhenClientExists() throws ResourceNotFound {
        // Arrange
        when(clientRepository.findByUserId(1)).thenReturn(testClient);
        when(modelMapper.map(testClient, ClientDto.class)).thenReturn(testClientDto);

        // Act
        ClientDto result = clientService.getClientByUserId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    @DisplayName("getClientByUserId() should throw ResourceNotFound when client does not exist")
    void getClientByUserId_ShouldThrowResourceNotFound_WhenClientDoesNotExist() {
        // Arrange
        when(clientRepository.findByUserId(999)).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFound.class,
                () -> clientService.getClientByUserId(999));
    }

    @Test
    @DisplayName("getAllClients() should return list of all clients as DTOs")
    void getAllClients_ShouldReturnListOfClientDtos() {
        // Arrange
        Client client2 = new Client();
        client2.setId(2);
        ClientDto dto2 = new ClientDto();
        dto2.setId(2);

        when(clientRepository.findAll()).thenReturn(List.of(testClient, client2));
        when(modelMapper.map(testClient, ClientDto.class)).thenReturn(testClientDto);
        when(modelMapper.map(client2, ClientDto.class)).thenReturn(dto2);

        // Act
        List<ClientDto> result = clientService.getAllClients();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getAllClients() should return empty list when no clients exist")
    void getAllClients_ShouldReturnEmptyList_WhenNoClientsExist() {
        // Arrange
        when(clientRepository.findAll()).thenReturn(List.of());

        // Act
        List<ClientDto> result = clientService.getAllClients();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getClientByEmail() should return ClientDto when client exists")
    void getClientByEmail_ShouldReturnClientDto_WhenClientExists() throws ResourceNotFound {
        // Arrange
        when(clientRepository.findByUser_Email("client@example.com")).thenReturn(testClient);
        when(modelMapper.map(testClient, ClientDto.class)).thenReturn(testClientDto);

        // Act
        ClientDto result = clientService.getClientByEmail("client@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("Petar Stoyanov", result.getUserFullName());
    }

    @Test
    @DisplayName("getClientByEmail() should throw ResourceNotFound when client does not exist")
    void getClientByEmail_ShouldThrowResourceNotFound_WhenClientDoesNotExist() {
        // Arrange
        when(clientRepository.findByUser_Email("nonexistent@example.com")).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFound.class,
                () -> clientService.getClientByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("getClientById() should return ClientDto when client exists")
    void getClientById_ShouldReturnClientDto_WhenClientExists() throws ResourceNotFound {
        // Arrange
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        when(modelMapper.map(testClient, ClientDto.class)).thenReturn(testClientDto);

        // Act
        ClientDto result = clientService.getClientById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    @DisplayName("getClientById() should throw ResourceNotFound when client does not exist")
    void getClientById_ShouldThrowResourceNotFound_WhenClientDoesNotExist() {
        // Arrange
        when(clientRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFound.class,
                () -> clientService.getClientById(999));
    }

    @Test
    @DisplayName("addAddress() should add address to client and return DTO")
    void addAddress_ShouldAddAddressAndReturnDto() {
        // Arrange
        Address address = new Address();
        address.setId(10);
        address.setFormattedAddress("New Street 1");
        address.setCoordinates("42.0,23.0");

        AddressDto expectedDto = new AddressDto();
        expectedDto.setId(10);
        expectedDto.setFormattedAddress("New Street 1");

        when(clientRepository.getById(1)).thenReturn(testClient);
        when(clientRepository.save(testClient)).thenReturn(testClient);
        when(modelMapper.map(address, AddressDto.class)).thenReturn(expectedDto);

        // Act
        AddressDto result = clientService.addAddress(1, address);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getId());
        verify(clientRepository).save(testClient);
    }

    @Test
    @DisplayName("updateClient() should update client successfully")
    void updateClient_ShouldUpdateClientSuccessfully() throws ResourceNotFound {
        // Arrange
        ClientDto updateDto = new ClientDto();
        updateDto.setPhoneNumber("0899999999");

        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(testClient)).thenReturn(testClient);
        when(modelMapper.map(testClient, ClientDto.class)).thenReturn(testClientDto);

        // Act
        ClientDto result = clientService.updateClient(1, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("0899999999", testClient.getPhoneNumber());
        verify(clientRepository).save(testClient);
    }

    @Test
    @DisplayName("updateClient() should throw ResourceNotFound when client does not exist")
    void updateClient_ShouldThrowResourceNotFound_WhenClientDoesNotExist() {
        // Arrange
        ClientDto updateDto = new ClientDto();
        updateDto.setPhoneNumber("0899999999");

        when(clientRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFound.class,
                () -> clientService.updateClient(999, updateDto));
    }

    @Test
    @DisplayName("updateClientWithUser() should update client and user successfully")
    void updateClientWithUser_ShouldUpdateSuccessfully() throws ResourceNotFound, EmailNotAvailable, AddressLinkedToShipment {
        // Arrange
        ClientDto clientDto = new ClientDto();
        clientDto.setPhoneNumber("0877111222");

        UserDto userDto = new UserDto();
        userDto.setFirstName("Petar Updated");
        userDto.setLastName("Stoyanov");
        userDto.setEmail("client@example.com");

        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(testClient)).thenReturn(testClient);
        when(modelMapper.map(testClient, ClientDto.class)).thenReturn(testClientDto);

        // Act
        ClientDto result = clientService.updateClientWithUser(1, clientDto, userDto, null);

        // Assert
        assertNotNull(result);
        verify(userRepository).save(testUser);
        verify(clientRepository).save(testClient);
    }

    @Test
    @DisplayName("updateClientWithUser() should throw ResourceNotFound when client does not exist")
    void updateClientWithUser_ShouldThrowResourceNotFound_WhenClientDoesNotExist() {
        // Arrange
        ClientDto clientDto = new ClientDto();
        UserDto userDto = new UserDto();

        when(clientRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFound.class,
                () -> clientService.updateClientWithUser(999, clientDto, userDto, null));
    }

    @Test
    @DisplayName("updateClientWithUser() should throw EmailNotAvailable when email belongs to different user")
    void updateClientWithUser_ShouldThrowEmailNotAvailable_WhenEmailTaken() {
        // Arrange
        ClientDto clientDto = new ClientDto();
        UserDto userDto = new UserDto();
        userDto.setEmail("taken@example.com");

        User otherUser = new User();
        otherUser.setId(99);

        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        when(userRepository.findByEmail("taken@example.com")).thenReturn(otherUser);

        // Act & Assert
        assertThrows(EmailNotAvailable.class,
                () -> clientService.updateClientWithUser(1, clientDto, userDto, null));
    }

    @Test
    @DisplayName("updateClientWithUser() should throw AddressLinkedToShipment when deleting linked address")
    void updateClientWithUser_ShouldThrowAddressLinkedToShipment_WhenDeletingLinkedAddress() {
        // Arrange
        ClientDto clientDto = new ClientDto();
        UserDto userDto = new UserDto();
        userDto.setEmail("client@example.com");

        List<Integer> addressIdsToDelete = List.of(10);

        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        when(shipmentRepository.existsByAddressId(10)).thenReturn(true);

        // Act & Assert
        assertThrows(AddressLinkedToShipment.class,
                () -> clientService.updateClientWithUser(1, clientDto, userDto, addressIdsToDelete));
    }

    @Test
    @DisplayName("updateClientWithUser() should throw AddressLinkedToShipment when modifying linked address")
    void updateClientWithUser_ShouldThrowAddressLinkedToShipment_WhenModifyingLinkedAddress() {
        // Arrange
        Address existingAddress = new Address();
        existingAddress.setId(10);
        existingAddress.setFormattedAddress("Old Street 1");
        existingAddress.setCoordinates("42.0,23.0");

        AddressDto modifiedAddressDto = new AddressDto();
        modifiedAddressDto.setId(10);
        modifiedAddressDto.setFormattedAddress("New Street 1");
        modifiedAddressDto.setCoordinates("42.0,23.0");

        ClientDto clientDto = new ClientDto();
        clientDto.setAddresses(List.of(modifiedAddressDto));

        UserDto userDto = new UserDto();
        userDto.setEmail("client@example.com");

        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        when(addressRepository.findById(10)).thenReturn(Optional.of(existingAddress));
        when(shipmentRepository.existsByAddressId(10)).thenReturn(true);

        // Act & Assert
        assertThrows(AddressLinkedToShipment.class,
                () -> clientService.updateClientWithUser(1, clientDto, userDto, null));
    }

    @Test
    @DisplayName("isAddressLinkedToShipment() should return true when address is linked")
    void isAddressLinkedToShipment_ShouldReturnTrue_WhenLinked() {
        // Arrange
        when(shipmentRepository.existsByAddressId(10)).thenReturn(true);

        // Act
        boolean result = clientService.isAddressLinkedToShipment(10);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isAddressLinkedToShipment() should return false when address is not linked")
    void isAddressLinkedToShipment_ShouldReturnFalse_WhenNotLinked() {
        // Arrange
        when(shipmentRepository.existsByAddressId(10)).thenReturn(false);

        // Act
        boolean result = clientService.isAddressLinkedToShipment(10);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("deleteClient() should delete client successfully")
    void deleteClient_ShouldDeleteClientSuccessfully() throws ResourceNotFound {
        // Arrange
        when(clientRepository.existsById(1)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(1);

        // Act
        clientService.deleteClient(1);

        // Assert
        verify(clientRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("deleteClient() should throw ResourceNotFound when client does not exist")
    void deleteClient_ShouldThrowResourceNotFound_WhenClientDoesNotExist() {
        // Arrange
        when(clientRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFound.class,
                () -> clientService.deleteClient(999));
    }

    @Test
    @DisplayName("anonymizeClient() should anonymize client data successfully")
    void anonymizeClient_ShouldAnonymizeSuccessfully() throws ResourceNotFound, CannotDeleteClient {
        // Arrange
        Address address = new Address();
        address.setId(10);
        address.setFormattedAddress("123 Main St");
        address.setCoordinates("42.6977,23.3219");
        testClient.getAddresses().add(address);

        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        when(shipmentRepository.findActiveShipmentsByClientId(1, Status.DELIVERED)).thenReturn(List.of());

        // Act
        clientService.anonymizeClient(1, 99);

        // Assert
        assertEquals("DELETED", testUser.getFirstName());
        assertEquals("USER_1", testUser.getLastName());
        assertEquals("deleted_1@anonymized.local", testUser.getEmail());
        assertEquals("0000000000", testClient.getPhoneNumber());
        assertEquals("Address removed - GDPR", address.getFormattedAddress());
        assertEquals("0.0,0.0", address.getCoordinates());
        verify(userRepository).save(testUser);
        verify(clientRepository).save(testClient);
    }

    @Test
    @DisplayName("anonymizeClient() should throw ResourceNotFound when client does not exist")
    void anonymizeClient_ShouldThrowResourceNotFound_WhenClientDoesNotExist() {
        // Arrange
        when(clientRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFound.class,
                () -> clientService.anonymizeClient(999, 1));
    }

    @Test
    @DisplayName("anonymizeClient() should throw CannotDeleteClient when deleting own account")
    void anonymizeClient_ShouldThrowCannotDeleteClient_WhenDeletingSelf() {
        // Arrange
        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));

        // Act & Assert
        CannotDeleteClient exception = assertThrows(CannotDeleteClient.class,
                () -> clientService.anonymizeClient(1, 1));
        assertEquals("You cannot delete your own account.", exception.getMessage());
    }

    @Test
    @DisplayName("anonymizeClient() should throw CannotDeleteClient when client has active shipments")
    void anonymizeClient_ShouldThrowCannotDeleteClient_WhenActiveShipmentsExist() {
        // Arrange
        Shipment activeShipment = new Shipment();
        activeShipment.setId(1);

        when(clientRepository.findById(1)).thenReturn(Optional.of(testClient));
        when(shipmentRepository.findActiveShipmentsByClientId(1, Status.DELIVERED))
                .thenReturn(List.of(activeShipment));

        // Act & Assert
        CannotDeleteClient exception = assertThrows(CannotDeleteClient.class,
                () -> clientService.anonymizeClient(1, 99));
        assertEquals("Cannot delete client with active shipments. Please wait until all shipments are delivered.",
                exception.getMessage());
    }
}
