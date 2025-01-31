package nbu.team11.services.contracts;

import nbu.team11.dtos.ClientDto;
import nbu.team11.services.exceptions.ResourceNotFound;

public interface IClientService {
    ClientDto createClient(ClientDto clientDto);
    ClientDto getClientByUserId(Integer userId) throws ResourceNotFound;
    ClientDto getClientById(Integer id) throws ResourceNotFound;
    ClientDto updateClient(Integer id, ClientDto updatedClient) throws ResourceNotFound;
    void deleteClient(Integer id) throws ResourceNotFound;
}

