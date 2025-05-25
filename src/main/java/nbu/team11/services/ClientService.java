package nbu.team11.services;

import nbu.team11.dtos.AddressDto;
import nbu.team11.dtos.ClientDto;
import nbu.team11.entities.Address;
import nbu.team11.entities.Client;
import nbu.team11.entities.User;
import nbu.team11.repositories.AddressRepository;
import nbu.team11.repositories.ClientRepository;
import nbu.team11.repositories.UserRepository;
import nbu.team11.services.contracts.IClientService;
import nbu.team11.services.exceptions.ResourceNotFound;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private AddressService addressService;

    @Autowired private AddressRepository addressRepository;
    @Autowired private UserRepository userRepository;

    @Override
    public ClientDto createClient(ClientDto clientDto, Integer userId) throws ResourceNotFound {
        User user = userRepository.findById(userId)
                .orElseThrow(ResourceNotFound::new);

        Address address = addressService.create(clientDto.getAddressDto());

        Client client = new Client();
        client.setUser(user);
        client.setPhoneNumber(clientDto.getPhoneNumber());
        client.setAddress(address);
        Client savedClient = clientRepository.save(client);

        ClientDto out = modelMapper.map(savedClient, ClientDto.class);
        out.setAddressDto(modelMapper.map(address, AddressDto.class));
        return out;
    }

    public ClientDto getClientByUserId(Integer userId) throws ResourceNotFound {
        Client client = clientRepository.findByUserId(userId);
        if (client == null) {
            throw new ResourceNotFound();
        }
        return modelMapper.map(client, ClientDto.class);
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

    public ClientDto updateClient(Integer id, ClientDto updatedClient) throws ResourceNotFound {
        Client existingClient = clientRepository.findById(id).orElse(null);
        if (existingClient == null) {
            throw new ResourceNotFound();
        }
        if (updatedClient.getPhoneNumber() != null) {
            existingClient.setPhoneNumber(updatedClient.getPhoneNumber());
        }
        existingClient = clientRepository.save(existingClient);
        return modelMapper.map(existingClient, ClientDto.class);
    }

    public void deleteClient(Integer id) throws ResourceNotFound {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFound();
        }
        clientRepository.deleteById(id);
    }
}
