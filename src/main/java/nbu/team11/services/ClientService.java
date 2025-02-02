package nbu.team11.services;

import nbu.team11.dtos.ClientDto;
import nbu.team11.entities.Client;
import nbu.team11.repositories.ClientRepository;
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

    public ClientDto createClient(ClientDto clientDto) {
        Client client = modelMapper.map(clientDto, Client.class);
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
