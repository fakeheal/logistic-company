package nbu.team11.services;

import nbu.team11.dtos.AddressDto;
import nbu.team11.dtos.ClientDto;
import nbu.team11.entities.Address;
import nbu.team11.entities.Client;
import nbu.team11.entities.Shipment;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.Status;
import nbu.team11.repositories.AddressRepository;
import nbu.team11.repositories.ClientRepository;
import nbu.team11.repositories.ShipmentRepository;
import nbu.team11.repositories.UserRepository;
import nbu.team11.services.contracts.IClientService;
import nbu.team11.services.exceptions.CannotDeleteClient;
import nbu.team11.services.exceptions.ResourceNotFound;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
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
