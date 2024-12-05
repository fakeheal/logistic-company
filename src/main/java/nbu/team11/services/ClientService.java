package nbu.team11.services;

import nbu.team11.entities.Client;
import nbu.team11.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//TODO: Revise all methods
//TODO: Test all methods
//TODO: Exception handling
//TODO: Create interface
@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    public Client getClientByUserId(Integer userId) {
        return clientRepository.findByUserId(userId);
    }

    public Client getClientById(Integer id) {
        return clientRepository.findById(id).orElse(null);
    }

    public Client updateClient(Integer id, Client updatedClient) {
        Client client = getClientById(id);
        if (client == null) return null;
        client.setPhoneNumber(updatedClient.getPhoneNumber());
        return clientRepository.save(client);
    }

    public void deleteClient(Integer id) {
        clientRepository.deleteById(id);
    }
}
