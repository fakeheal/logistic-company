package nbu.team11.services;

import nbu.team11.dtos.ClientDto;
import nbu.team11.entities.Client;
import nbu.team11.repositories.ClientRepository;
import nbu.team11.services.ClientService;
import nbu.team11.services.exceptions.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ClientService clientService;

    private Client client;
    private ClientDto clientDto;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1);
        client.setPhoneNumber("0888123456");

        clientDto = new ClientDto();
        clientDto.setId(1);
        clientDto.setPhoneNumber("0888123456");
    }

    @Test
    void testCreateClient() {
        when(modelMapper.map(clientDto, Client.class)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(modelMapper.map(client, ClientDto.class)).thenReturn(clientDto);

        ClientDto savedClient = clientService.createClient(clientDto);

        assertNotNull(savedClient);
        assertEquals(clientDto.getId(), savedClient.getId());
        assertEquals(clientDto.getPhoneNumber(), savedClient.getPhoneNumber());
    }

    @Test
    void testGetClientByUserId_Success() throws ResourceNotFound {
        when(clientRepository.findByUserId(1)).thenReturn(client);
        when(modelMapper.map(client, ClientDto.class)).thenReturn(clientDto);

        ClientDto foundClient = clientService.getClientByUserId(1);

        assertNotNull(foundClient);
        assertEquals(clientDto.getId(), foundClient.getId());
        assertEquals(clientDto.getPhoneNumber(), foundClient.getPhoneNumber());
    }

    @Test
    void testGetClientByUserId_NotFound() {
        when(clientRepository.findByUserId(2)).thenReturn(null);

        assertThrows(ResourceNotFound.class, () -> clientService.getClientByUserId(2));
    }

    @Test
    void testGetClientById_Success() throws ResourceNotFound {
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));
        when(modelMapper.map(client, ClientDto.class)).thenReturn(clientDto);

        ClientDto foundClient = clientService.getClientById(1);

        assertNotNull(foundClient);
        assertEquals(clientDto.getId(), foundClient.getId());
        assertEquals(clientDto.getPhoneNumber(), foundClient.getPhoneNumber());
    }

    @Test
    void testGetClientById_NotFound() {
        when(clientRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> clientService.getClientById(2));
    }

    @Test
    void testUpdateClient_Success() throws ResourceNotFound {
        ClientDto updatedClientDto = new ClientDto();
        updatedClientDto.setPhoneNumber("0899123456");

        when(clientRepository.findById(1)).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);
        when(modelMapper.map(client, ClientDto.class)).thenReturn(updatedClientDto);

        ClientDto updatedClient = clientService.updateClient(1, updatedClientDto);

        assertNotNull(updatedClient);
        assertEquals(updatedClientDto.getPhoneNumber(), updatedClient.getPhoneNumber());
    }

    @Test
    void testUpdateClient_NotFound() {
        when(clientRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> clientService.updateClient(2, new ClientDto()));
    }

    @Test
    void testDeleteClient_Success() throws ResourceNotFound {
        when(clientRepository.existsById(1)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(1);

        assertDoesNotThrow(() -> clientService.deleteClient(1));

        verify(clientRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteClient_NotFound() {
        when(clientRepository.existsById(2)).thenReturn(false);

        assertThrows(ResourceNotFound.class, () -> clientService.deleteClient(2));
    }
}
