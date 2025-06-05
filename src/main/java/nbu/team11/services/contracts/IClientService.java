package nbu.team11.services.contracts;

import nbu.team11.dtos.ClientDto;
import nbu.team11.services.exceptions.ResourceNotFound;

import java.util.List;

public interface IClientService {
    ClientDto createClient(ClientDto clientDto, Integer userId) throws ResourceNotFound;
    ClientDto getClientByUserId(Integer userId) throws ResourceNotFound;
    ClientDto getClientById(Integer id) throws ResourceNotFound;
    ClientDto updateClient(Integer id, ClientDto updatedClient) throws ResourceNotFound;
    void deleteClient(Integer id) throws ResourceNotFound;
    List<ClientDto> getAllClients();
}

