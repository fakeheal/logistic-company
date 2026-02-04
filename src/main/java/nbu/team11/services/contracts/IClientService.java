package nbu.team11.services.contracts;

import nbu.team11.dtos.ClientDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.services.exceptions.AddressLinkedToShipment;
import nbu.team11.services.exceptions.CannotDeleteClient;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.ResourceNotFound;

import java.util.List;

public interface IClientService {
    ClientDto createClient(ClientDto clientDto, Integer userId) throws ResourceNotFound;
    ClientDto createClientWithGeneratedPassword(ClientDto clientDto, UserDto userDto) throws EmailNotAvailable;
    ClientDto getClientByUserId(Integer userId) throws ResourceNotFound;
    ClientDto getClientById(Integer id) throws ResourceNotFound;
    ClientDto updateClient(Integer id, ClientDto updatedClient) throws ResourceNotFound;
    ClientDto updateClientWithUser(Integer clientId, ClientDto clientDto, UserDto userDto, List<Integer> addressIdsToDelete) throws ResourceNotFound, EmailNotAvailable, AddressLinkedToShipment;
    void deleteClient(Integer id) throws ResourceNotFound;
    void anonymizeClient(Integer clientId, Integer currentUserId) throws ResourceNotFound, CannotDeleteClient;
    List<ClientDto> getAllClients();
    boolean isAddressLinkedToShipment(Integer addressId);
}

