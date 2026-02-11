package nbu.team11.services;

import nbu.team11.dtos.AddressDto;
import nbu.team11.entities.Address;
import nbu.team11.repositories.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressService Tests")
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressService addressService;

    private Address testAddress;
    private AddressDto testAddressDto;

    @BeforeEach
    void setUp() {
        testAddress = new Address();
        testAddress.setId(1);
        testAddress.setFormattedAddress("123 Main St, Sofia, Bulgaria");
        testAddress.setCoordinates("42.6977,23.3219");

        testAddressDto = new AddressDto();
        testAddressDto.setId(1);
        testAddressDto.setFormattedAddress("123 Main St, Sofia, Bulgaria");
        testAddressDto.setCoordinates("42.6977,23.3219");
    }

    @Test
    @DisplayName("create() should save and return new address")
    void create_ShouldSaveAndReturnNewAddress() {
        // Arrange
        AddressDto inputDto = new AddressDto();
        inputDto.setFormattedAddress("456 Oak Ave, Plovdiv, Bulgaria");
        inputDto.setCoordinates("42.1354,24.7453");

        Address mappedEntity = new Address();
        mappedEntity.setFormattedAddress("456 Oak Ave, Plovdiv, Bulgaria");
        mappedEntity.setCoordinates("42.1354,24.7453");

        Address savedEntity = new Address();
        savedEntity.setId(2);
        savedEntity.setFormattedAddress("456 Oak Ave, Plovdiv, Bulgaria");
        savedEntity.setCoordinates("42.1354,24.7453");

        when(modelMapper.map(inputDto, Address.class)).thenReturn(mappedEntity);
        when(addressRepository.save(mappedEntity)).thenReturn(savedEntity);

        // Act
        Address result = addressService.create(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("456 Oak Ave, Plovdiv, Bulgaria", result.getFormattedAddress());
        assertEquals("42.1354,24.7453", result.getCoordinates());
        verify(addressRepository, times(1)).save(mappedEntity);
    }

    @Test
    @DisplayName("findById() should return address DTO when address exists")
    void findById_ShouldReturnAddressDto_WhenAddressExists() {
        // Arrange
        when(addressRepository.findById(1)).thenReturn(Optional.of(testAddress));
        when(modelMapper.map(testAddress, AddressDto.class)).thenReturn(testAddressDto);

        // Act
        Optional<AddressDto> result = addressService.findById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("123 Main St, Sofia, Bulgaria", result.get().getFormattedAddress());
        assertEquals("42.6977,23.3219", result.get().getCoordinates());
    }

    @Test
    @DisplayName("findById() should return empty Optional when address does not exist")
    void findById_ShouldReturnEmptyOptional_WhenAddressDoesNotExist() {
        // Arrange
        when(addressRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<AddressDto> result = addressService.findById(999);

        // Assert
        assertTrue(result.isEmpty());
    }
}
