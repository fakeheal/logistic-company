package nbu.team11.services;

import nbu.team11.dtos.AddressDto;
import nbu.team11.dtos.OfficeDto;
import nbu.team11.entities.Address;
import nbu.team11.entities.Office;
import nbu.team11.repositories.AddressRepository;
import nbu.team11.repositories.OfficeRepository;
import nbu.team11.services.exceptions.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OfficeService Tests")
class OfficeServiceTest {

    @Mock
    private OfficeRepository officeRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OfficeService officeService;

    private Office testOffice;
    private OfficeDto testOfficeDto;
    private Address testAddress;
    private AddressDto testAddressDto;

    @BeforeEach
    void setUp() {
        // Setup test Address
        testAddress = new Address();
        testAddress.setId(1);
        testAddress.setFormattedAddress("123 Main St, Sofia, Bulgaria");
        testAddress.setCoordinates("42.6977,23.3219");

        // Setup test AddressDto
        testAddressDto = new AddressDto();
        testAddressDto.setId(1);
        testAddressDto.setFormattedAddress("123 Main St, Sofia, Bulgaria");
        testAddressDto.setCoordinates("42.6977,23.3219");

        // Setup test Office
        testOffice = new Office();
        testOffice.setId(1);
        testOffice.setTitle("Central Office");
        testOffice.setAddress(testAddress);

        // Setup test OfficeDto
        testOfficeDto = new OfficeDto();
        testOfficeDto.setId(1);
        testOfficeDto.setTitle("Central Office");
        testOfficeDto.setAddressDto(testAddressDto);
        testOfficeDto.setCreatedOn(Instant.now());
        testOfficeDto.setUpdatedOn(Instant.now());
    }

    @Test
    @DisplayName("getAll() should return list of all offices as DTOs")
    void getAll_ShouldReturnListOfOfficeDtos() {
        // Arrange
        Office office2 = new Office();
        office2.setId(2);
        office2.setTitle("North Office");

        OfficeDto officeDto2 = new OfficeDto();
        officeDto2.setId(2);
        officeDto2.setTitle("North Office");

        List<Office> offices = Arrays.asList(testOffice, office2);

        when(officeRepository.findAll()).thenReturn(offices);
        when(modelMapper.map(testOffice, OfficeDto.class)).thenReturn(testOfficeDto);
        when(modelMapper.map(office2, OfficeDto.class)).thenReturn(officeDto2);

        // Act
        List<OfficeDto> result = officeService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Central Office", result.get(0).getTitle());
        assertEquals("North Office", result.get(1).getTitle());
        verify(officeRepository, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(Office.class), eq(OfficeDto.class));
    }

    @Test
    @DisplayName("getAll() should return empty list when no offices exist")
    void getAll_ShouldReturnEmptyList_WhenNoOfficesExist() {
        // Arrange
        when(officeRepository.findAll()).thenReturn(List.of());

        // Act
        List<OfficeDto> result = officeService.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(officeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getById() should return office DTO when office exists")
    void getById_ShouldReturnOfficeDto_WhenOfficeExists() throws ResourceNotFound {
        // Arrange
        when(officeRepository.findById(1)).thenReturn(Optional.of(testOffice));
        when(modelMapper.map(testOffice, OfficeDto.class)).thenReturn(testOfficeDto);

        // Act
        OfficeDto result = officeService.getById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Central Office", result.getTitle());
        verify(officeRepository, times(1)).findById(1);
        verify(modelMapper, times(1)).map(testOffice, OfficeDto.class);
    }

    @Test
    @DisplayName("getById() should throw ResourceNotFound when office does not exist")
    void getById_ShouldThrowResourceNotFound_WhenOfficeDoesNotExist() {
        // Arrange
        when(officeRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFound.class, () -> officeService.getById(999));
        verify(officeRepository, times(1)).findById(999);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    @DisplayName("paginate() should return paginated offices without search")
    void paginate_ShouldReturnPaginatedOffices_WithoutSearch() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Office> officePage = new PageImpl<>(List.of(testOffice), pageable, 1);

        when(officeRepository.findAll(pageable)).thenReturn(officePage);
        when(modelMapper.map(testOffice, OfficeDto.class)).thenReturn(testOfficeDto);

        // Act
        Page<OfficeDto> result = officeService.paginate(0, 10, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Central Office", result.getContent().get(0).getTitle());
        verify(officeRepository, times(1)).findAll(pageable);
        verify(officeRepository, never()).findByAddressContaining(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("paginate() should return paginated offices with search")
    void paginate_ShouldReturnPaginatedOffices_WithSearch() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Office> officePage = new PageImpl<>(List.of(testOffice), pageable, 1);

        when(officeRepository.findByAddressContaining("Sofia", pageable)).thenReturn(officePage);
        when(modelMapper.map(testOffice, OfficeDto.class)).thenReturn(testOfficeDto);

        // Act
        Page<OfficeDto> result = officeService.paginate(0, 10, "Sofia");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Central Office", result.getContent().get(0).getTitle());
        verify(officeRepository, times(1)).findByAddressContaining("Sofia", pageable);
        verify(officeRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("paginate() should ignore blank search string")
    void paginate_ShouldIgnoreBlankSearch() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Office> officePage = new PageImpl<>(List.of(testOffice), pageable, 1);

        when(officeRepository.findAll(pageable)).thenReturn(officePage);
        when(modelMapper.map(testOffice, OfficeDto.class)).thenReturn(testOfficeDto);

        // Act
        Page<OfficeDto> result = officeService.paginate(0, 10, "   ");

        // Assert
        assertNotNull(result);
        verify(officeRepository, times(1)).findAll(pageable);
        verify(officeRepository, never()).findByAddressContaining(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("create() should create new office with address")
    void create_ShouldCreateNewOfficeWithAddress() {
        // Arrange
        String title = "New Office";
        String formattedAddress = "456 Park Ave, Plovdiv, Bulgaria";
        String coordinates = "42.1354,24.7453";

        Address savedAddress = new Address();
        savedAddress.setId(2);
        savedAddress.setFormattedAddress(formattedAddress);
        savedAddress.setCoordinates(coordinates);

        Office savedOffice = new Office();
        savedOffice.setId(2);
        savedOffice.setTitle(title);
        savedOffice.setAddress(savedAddress);

        OfficeDto expectedDto = new OfficeDto();
        expectedDto.setId(2);
        expectedDto.setTitle(title);

        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);
        when(officeRepository.save(any(Office.class))).thenReturn(savedOffice);
        when(modelMapper.map(savedOffice, OfficeDto.class)).thenReturn(expectedDto);

        // Act
        OfficeDto result = officeService.create(title, formattedAddress, coordinates);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals(title, result.getTitle());
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(officeRepository, times(1)).save(any(Office.class));
        verify(modelMapper, times(1)).map(savedOffice, OfficeDto.class);
    }

    @Test
    @DisplayName("update() should update existing office")
    void update_ShouldUpdateExistingOffice() throws ResourceNotFound {
        // Arrange
        Integer officeId = 1;
        String newTitle = "Updated Central Office";
        String newAddress = "789 New St, Sofia, Bulgaria";
        String newCoordinates = "42.7000,23.3300";

        Office existingOffice = new Office();
        existingOffice.setId(officeId);
        existingOffice.setTitle("Old Title");
        existingOffice.setAddress(testAddress);

        when(officeRepository.findById(officeId)).thenReturn(Optional.of(existingOffice));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);
        when(officeRepository.save(any(Office.class))).thenReturn(existingOffice);
        when(modelMapper.map(existingOffice, OfficeDto.class)).thenReturn(testOfficeDto);

        // Act
        OfficeDto result = officeService.update(officeId, newTitle, newAddress, newCoordinates);

        // Assert
        assertNotNull(result);
        assertEquals(newTitle, existingOffice.getTitle());
        assertEquals(newAddress, testAddress.getFormattedAddress());
        assertEquals(newCoordinates, testAddress.getCoordinates());
        verify(officeRepository, times(1)).findById(officeId);
        verify(addressRepository, times(1)).save(testAddress);
        verify(officeRepository, times(1)).save(existingOffice);
        verify(modelMapper, times(1)).map(existingOffice, OfficeDto.class);
    }

    @Test
    @DisplayName("update() should throw ResourceNotFound when office does not exist")
    void update_ShouldThrowResourceNotFound_WhenOfficeDoesNotExist() {
        // Arrange
        when(officeRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFound.class, () ->
                officeService.update(999, "Title", "Address", "Coords")
        );
        verify(officeRepository, times(1)).findById(999);
        verify(addressRepository, never()).save(any());
        verify(officeRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete() should delete office and associated address")
    void delete_ShouldDeleteOfficeAndAddress() throws ResourceNotFound {
        // Arrange
        when(officeRepository.findById(1)).thenReturn(Optional.of(testOffice));
        doNothing().when(officeRepository).delete(testOffice);
        doNothing().when(addressRepository).delete(testAddress);

        // Act
        officeService.delete(1);

        // Assert
        verify(officeRepository, times(1)).findById(1);
        verify(officeRepository, times(1)).delete(testOffice);
        verify(addressRepository, times(1)).delete(testAddress);
    }

    @Test
    @DisplayName("delete() should delete office even when address is null")
    void delete_ShouldDeleteOffice_WhenAddressIsNull() throws ResourceNotFound {
        // Arrange
        testOffice.setAddress(null);
        when(officeRepository.findById(1)).thenReturn(Optional.of(testOffice));
        doNothing().when(officeRepository).delete(testOffice);

        // Act
        officeService.delete(1);

        // Assert
        verify(officeRepository, times(1)).findById(1);
        verify(officeRepository, times(1)).delete(testOffice);
        verify(addressRepository, never()).delete(any());
    }

    @Test
    @DisplayName("delete() should throw ResourceNotFound when office does not exist")
    void delete_ShouldThrowResourceNotFound_WhenOfficeDoesNotExist() {
        // Arrange
        when(officeRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFound.class, () -> officeService.delete(999));
        verify(officeRepository, times(1)).findById(999);
        verify(officeRepository, never()).delete(any());
        verify(addressRepository, never()).delete(any());
    }
}
