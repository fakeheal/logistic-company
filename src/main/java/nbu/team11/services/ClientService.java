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
import nbu.team11.services.contracts.IClientService;
import nbu.team11.services.exceptions.AddressLinkedToShipment;
import nbu.team11.services.exceptions.CannotDeleteClient;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.ResourceNotFound;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//TODO: Revise all methods
//TODO: Test all methods
//TODO: Exception handling
//TODO: Create interface
@Service
public class ClientService implements IClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ClientDto createClient(ClientDto clientDto, Integer userId) throws ResourceNotFound {
        User user = userRepository.findById(userId)
                .orElseThrow(ResourceNotFound::new);

        Client client = new Client();
        client.setUser(user);
        client.setPhoneNumber(clientDto.getPhoneNumber());

        if (clientDto.getAddresses() != null && !clientDto.getAddresses().isEmpty()) {
            Type listType = new TypeToken<List<AddressDto>>() {}.getType(); // for clarity; not strictly required
            for (AddressDto adto : clientDto.getAddresses()) {
                Address addr = modelMapper.map(adto, Address.class);
                client.addAddress(addr);
            }
        }

        Client savedClient = clientRepository.save(client);

        return modelMapper.map(savedClient, ClientDto.class);
    }

    @Override
    @Transactional
    public ClientDto createClientWithGeneratedPassword(ClientDto clientDto, UserDto userDto) throws EmailNotAvailable {
        // Check if email is already in use
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new EmailNotAvailable();
        }

        // Generate a secure random password
        String generatedPassword = generateSecurePassword();

        // Create User entity
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(generatedPassword));
        user.setRole(Role.CLIENT);
        User savedUser = userRepository.save(user);

        // Create Client entity
        Client client = new Client();
        client.setUser(savedUser);
        client.setPhoneNumber(clientDto.getPhoneNumber());

        if (clientDto.getAddresses() != null && !clientDto.getAddresses().isEmpty()) {
            for (AddressDto adto : clientDto.getAddresses()) {
                Address addr = modelMapper.map(adto, Address.class);
                client.addAddress(addr);
            }
        }

        Client savedClient = clientRepository.save(client);

        // @TODO: Send email with the generated password to the user
        // Email should be sent to: userDto.getEmail()
        // Password to include in email: generatedPassword
        System.out.println("TODO: Send email to " + userDto.getEmail() + " with password: " + generatedPassword);

        return modelMapper.map(savedClient, ClientDto.class);
    }

    private String generateSecurePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    public ClientDto getClientByUserId(Integer userId) throws ResourceNotFound {
        Client client = clientRepository.findByUserId(userId);
        if (client == null) {
            throw new ResourceNotFound();
        }
        return modelMapper.map(client, ClientDto.class);
    }

    public List<ClientDto> getAllClients() {
        List<Client> allClients = clientRepository.findAll();
        return allClients.stream()
                .map(client -> modelMapper.map(client, ClientDto.class))
                .collect(Collectors.toList());
    }

    public ClientDto getClientByEmail(String email) throws ResourceNotFound {
        Client client = clientRepository.findByUser_Email(email);
        if (client == null) {
            throw new ResourceNotFound();
        }
        return modelMapper.map(client, ClientDto.class);
    }

    public ClientDto getClientById(Integer id) throws ResourceNotFound {
        Client client = clientRepository.findById(id).orElse(null);
        if (client == null) {
            throw new ResourceNotFound();
        }
        return modelMapper.map(client, ClientDto.class);
    }

    public AddressDto addAddress(Integer id, Address address){
        Client client = clientRepository.getById(id);
        client.addAddress(address);
        clientRepository.save(client);
        return modelMapper.map(address, AddressDto.class);
    }

    @Transactional
    public ClientDto updateClient(Integer id, ClientDto updatedClient) throws ResourceNotFound {
        Client existingClient = clientRepository.findById(id).orElse(null);
        if (existingClient == null) {
            throw new ResourceNotFound();
        }

        if (updatedClient.getPhoneNumber() != null) {
            existingClient.setPhoneNumber(updatedClient.getPhoneNumber());
        }

        if (updatedClient.getAddresses() != null) {
            existingClient.getAddresses().clear();

            for (AddressDto adto : updatedClient.getAddresses()) {
                Address addr = modelMapper.map(adto, Address.class);
                existingClient.addAddress(addr);
            }
        }

        Client saved = clientRepository.save(existingClient);
        return modelMapper.map(saved, ClientDto.class);
    }

    @Override
    @Transactional
    public ClientDto updateClientWithUser(Integer clientId, ClientDto clientDto, UserDto userDto, List<Integer> addressIdsToDelete)
            throws ResourceNotFound, EmailNotAvailable, AddressLinkedToShipment {
        Client client = clientRepository.findById(clientId).orElse(null);
        if (client == null) {
            throw new ResourceNotFound();
        }

        User user = client.getUser();

        // Check if email is being changed and if the new email is available
        if (!user.getEmail().equals(userDto.getEmail())) {
            User existingUserWithEmail = userRepository.findByEmail(userDto.getEmail());
            if (existingUserWithEmail != null && !existingUserWithEmail.getId().equals(user.getId())) {
                throw new EmailNotAvailable();
            }
        }

        // Check if any addresses to be deleted are linked to shipments
        if (addressIdsToDelete != null && !addressIdsToDelete.isEmpty()) {
            for (Integer addressId : addressIdsToDelete) {
                if (shipmentRepository.existsByAddressId(addressId)) {
                    throw new AddressLinkedToShipment("Cannot delete address that is linked to shipments.");
                }
            }
        }

        // Check if any existing addresses being updated are linked to shipments
        if (clientDto.getAddresses() != null) {
            for (AddressDto addressDto : clientDto.getAddresses()) {
                if (addressDto.getId() != null) {
                    // Find the existing address
                    Address existingAddress = addressRepository.findById(addressDto.getId()).orElse(null);
                    if (existingAddress != null) {
                        // Check if address data is being modified
                        boolean isModified = !existingAddress.getFormattedAddress().equals(addressDto.getFormattedAddress())
                                || !existingAddress.getCoordinates().equals(addressDto.getCoordinates());

                        if (isModified && shipmentRepository.existsByAddressId(addressDto.getId())) {
                            throw new AddressLinkedToShipment("Cannot update address that is linked to shipments.");
                        }
                    }
                }
            }
        }

        // Update User fields
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        userRepository.save(user);

        // Update Client phone number
        if (clientDto.getPhoneNumber() != null) {
            client.setPhoneNumber(clientDto.getPhoneNumber());
        }

        // Handle address deletions
        if (addressIdsToDelete != null && !addressIdsToDelete.isEmpty()) {
            client.getAddresses().removeIf(addr -> addressIdsToDelete.contains(addr.getId()));
        }

        // Handle address updates and additions
        if (clientDto.getAddresses() != null) {
            for (AddressDto addressDto : clientDto.getAddresses()) {
                if (addressDto.getId() != null) {
                    // Update existing address
                    for (Address addr : client.getAddresses()) {
                        if (addr.getId().equals(addressDto.getId())) {
                            addr.setFormattedAddress(addressDto.getFormattedAddress());
                            addr.setCoordinates(addressDto.getCoordinates());
                            break;
                        }
                    }
                } else {
                    // Add new address
                    Address newAddress = new Address();
                    newAddress.setFormattedAddress(addressDto.getFormattedAddress());
                    newAddress.setCoordinates(addressDto.getCoordinates());
                    client.addAddress(newAddress);
                }
            }
        }

        Client saved = clientRepository.save(client);
        return modelMapper.map(saved, ClientDto.class);
    }

    @Override
    public boolean isAddressLinkedToShipment(Integer addressId) {
        return shipmentRepository.existsByAddressId(addressId);
    }

    @Transactional
    public void deleteClient(Integer id) throws ResourceNotFound {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFound();
        }
        clientRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void anonymizeClient(Integer clientId, Integer currentUserId) throws ResourceNotFound, CannotDeleteClient {
        Client client = clientRepository.findById(clientId).orElse(null);
        if (client == null) {
            throw new ResourceNotFound();
        }

        User user = client.getUser();

        // Prevent self-deletion
        if (user.getId().equals(currentUserId)) {
            throw new CannotDeleteClient("You cannot delete your own account.");
        }

        // Check for active (undelivered) shipments
        List<Shipment> activeShipments = shipmentRepository.findActiveShipmentsByClientId(clientId, Status.DELIVERED);
        if (!activeShipments.isEmpty()) {
            throw new CannotDeleteClient("Cannot delete client with active shipments. Please wait until all shipments are delivered.");
        }

        // Anonymize User fields
        user.setFirstName("DELETED");
        user.setLastName("USER_" + user.getId());
        user.setEmail("deleted_" + user.getId() + "@anonymized.local");
        user.setPassword(UUID.randomUUID().toString());
        userRepository.save(user);

        // Anonymize Client phone number
        client.setPhoneNumber("0000000000");

        // Anonymize all associated Address records
        for (Address address : client.getAddresses()) {
            address.setFormattedAddress("Address removed - GDPR");
            address.setCoordinates("0.0,0.0");
        }

        clientRepository.save(client);
    }
}
