package nbu.team11.services;

import nbu.team11.dtos.CountryDto;
import nbu.team11.entities.Country;
import nbu.team11.repositories.CountryRepository;
import nbu.team11.services.exceptions.CountryNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountryService Tests")
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CountryService countryService;

    private Country testCountry;
    private CountryDto testCountryDto;

    @BeforeEach
    void setUp() {
        // Setup test Country entity
        testCountry = Country.builder()
                .id(1)
                .name("Bulgaria")
                .createdOn(Instant.now())
                .updatedOn(Instant.now())
                .build();

        // Setup test CountryDto
        testCountryDto = new CountryDto();
        testCountryDto.setId(1);
        testCountryDto.setName("Bulgaria");
        testCountryDto.setCreatedOn(Instant.now());
        testCountryDto.setUpdatedOn(Instant.now());
    }

    @Test
    @DisplayName("getAllCountries() should return list of all countries as DTOs")
    void getAllCountries_ShouldReturnListOfCountryDtos() {
        // Arrange
        Country country2 = Country.builder()
                .id(2)
                .name("Romania")
                .build();

        CountryDto countryDto2 = new CountryDto();
        countryDto2.setId(2);
        countryDto2.setName("Romania");

        List<Country> countries = Arrays.asList(testCountry, country2);

        when(countryRepository.findAll()).thenReturn(countries);
        when(modelMapper.map(testCountry, CountryDto.class)).thenReturn(testCountryDto);
        when(modelMapper.map(country2, CountryDto.class)).thenReturn(countryDto2);

        // Act
        List<CountryDto> result = countryService.getAllCountries();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Bulgaria", result.get(0).getName());
        assertEquals("Romania", result.get(1).getName());
        verify(countryRepository, times(1)).findAll();
        verify(modelMapper, times(2)).map(any(Country.class), eq(CountryDto.class));
    }

    @Test
    @DisplayName("getAllCountries() should return empty list when no countries exist")
    void getAllCountries_ShouldReturnEmptyList_WhenNoCountriesExist() {
        // Arrange
        when(countryRepository.findAll()).thenReturn(List.of());

        // Act
        List<CountryDto> result = countryService.getAllCountries();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(countryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getCountryById() should return country DTO when country exists")
    void getCountryById_ShouldReturnCountryDto_WhenCountryExists() {
        // Arrange
        when(countryRepository.findById(1)).thenReturn(Optional.of(testCountry));
        when(modelMapper.map(testCountry, CountryDto.class)).thenReturn(testCountryDto);

        // Act
        CountryDto result = countryService.getCountryById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Bulgaria", result.getName());
        verify(countryRepository, times(1)).findById(1);
        verify(modelMapper, times(1)).map(testCountry, CountryDto.class);
    }

    @Test
    @DisplayName("getCountryById() should throw CountryNotFound when country does not exist")
    void getCountryById_ShouldThrowCountryNotFound_WhenCountryDoesNotExist() {
        // Arrange
        when(countryRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        CountryNotFound exception = assertThrows(CountryNotFound.class,
                () -> countryService.getCountryById(999));
        assertEquals("Country with ID 999 not found", exception.getMessage());
        verify(countryRepository, times(1)).findById(999);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    @DisplayName("createCountry() should create new country successfully")
    void createCountry_ShouldCreateNewCountrySuccessfully() {
        // Arrange
        CountryDto newCountryDto = new CountryDto();
        newCountryDto.setName("Greece");

        Country savedCountry = Country.builder()
                .id(3)
                .name("Greece")
                .build();

        CountryDto expectedDto = new CountryDto();
        expectedDto.setId(3);
        expectedDto.setName("Greece");

        when(countryRepository.existsByName("Greece")).thenReturn(false);
        when(countryRepository.save(any(Country.class))).thenReturn(savedCountry);
        when(modelMapper.map(savedCountry, CountryDto.class)).thenReturn(expectedDto);

        // Act
        CountryDto result = countryService.createCountry(newCountryDto);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getId());
        assertEquals("Greece", result.getName());
        verify(countryRepository, times(1)).existsByName("Greece");
        verify(countryRepository, times(1)).save(any(Country.class));
        verify(modelMapper, times(1)).map(savedCountry, CountryDto.class);
    }

    @Test
    @DisplayName("createCountry() should throw IllegalArgumentException when country name already exists")
    void createCountry_ShouldThrowIllegalArgumentException_WhenNameAlreadyExists() {
        // Arrange
        CountryDto newCountryDto = new CountryDto();
        newCountryDto.setName("Bulgaria");

        when(countryRepository.existsByName("Bulgaria")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> countryService.createCountry(newCountryDto));
        assertEquals("A country with this name already exists.", exception.getMessage());
        verify(countryRepository, times(1)).existsByName("Bulgaria");
        verify(countryRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateCountry() should update existing country successfully")
    void updateCountry_ShouldUpdateExistingCountrySuccessfully() {
        // Arrange
        Integer countryId = 1;
        CountryDto updateDto = new CountryDto();
        updateDto.setName("Bulgaria Updated");

        Country existingCountry = Country.builder()
                .id(countryId)
                .name("Bulgaria")
                .build();

        Country updatedCountry = Country.builder()
                .id(countryId)
                .name("Bulgaria Updated")
                .build();

        CountryDto expectedDto = new CountryDto();
        expectedDto.setId(countryId);
        expectedDto.setName("Bulgaria Updated");

        when(countryRepository.findById(countryId)).thenReturn(Optional.of(existingCountry));
        when(countryRepository.existsByName("Bulgaria Updated")).thenReturn(false);
        when(countryRepository.save(existingCountry)).thenReturn(updatedCountry);
        when(modelMapper.map(updatedCountry, CountryDto.class)).thenReturn(expectedDto);

        // Act
        CountryDto result = countryService.updateCountry(countryId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(countryId, result.getId());
        assertEquals("Bulgaria Updated", result.getName());
        verify(countryRepository, times(1)).findById(countryId);
        verify(countryRepository, times(1)).existsByName("Bulgaria Updated");
        verify(countryRepository, times(1)).save(existingCountry);
        verify(modelMapper, times(1)).map(updatedCountry, CountryDto.class);
    }

    @Test
    @DisplayName("updateCountry() should allow updating to same name for same country")
    void updateCountry_ShouldAllowSameNameForSameCountry() {
        // Arrange
        Integer countryId = 1;
        CountryDto updateDto = new CountryDto();
        updateDto.setName("Bulgaria");

        Country existingCountry = Country.builder()
                .id(countryId)
                .name("Bulgaria")
                .build();

        CountryDto expectedDto = new CountryDto();
        expectedDto.setId(countryId);
        expectedDto.setName("Bulgaria");

        when(countryRepository.findById(countryId)).thenReturn(Optional.of(existingCountry));
        when(countryRepository.existsByName("Bulgaria")).thenReturn(true);
        when(countryRepository.save(existingCountry)).thenReturn(existingCountry);
        when(modelMapper.map(existingCountry, CountryDto.class)).thenReturn(expectedDto);

        // Act
        CountryDto result = countryService.updateCountry(countryId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(countryId, result.getId());
        assertEquals("Bulgaria", result.getName());
        verify(countryRepository, times(1)).findById(countryId);
        verify(countryRepository, times(1)).save(existingCountry);
    }

    @Test
    @DisplayName("updateCountry() should throw CountryNotFound when country does not exist")
    void updateCountry_ShouldThrowCountryNotFound_WhenCountryDoesNotExist() {
        // Arrange
        CountryDto updateDto = new CountryDto();
        updateDto.setName("NonExistent");

        when(countryRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        CountryNotFound exception = assertThrows(CountryNotFound.class,
                () -> countryService.updateCountry(999, updateDto));
        assertEquals("Country with ID 999 not found", exception.getMessage());
        verify(countryRepository, times(1)).findById(999);
        verify(countryRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateCountry() should throw IllegalArgumentException when new name already exists for different country")
    void updateCountry_ShouldThrowIllegalArgumentException_WhenNewNameExistsForDifferentCountry() {
        // Arrange
        Integer countryId = 1;
        CountryDto updateDto = new CountryDto();
        updateDto.setName("Romania");

        Country existingCountry = Country.builder()
                .id(countryId)
                .name("Bulgaria")
                .build();

        when(countryRepository.findById(countryId)).thenReturn(Optional.of(existingCountry));
        when(countryRepository.existsByName("Romania")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> countryService.updateCountry(countryId, updateDto));
        assertEquals("A country with this name already exists.", exception.getMessage());
        verify(countryRepository, times(1)).findById(countryId);
        verify(countryRepository, times(1)).existsByName("Romania");
        verify(countryRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteCountry() should delete country successfully")
    void deleteCountry_ShouldDeleteCountrySuccessfully() {
        // Arrange
        when(countryRepository.existsById(1)).thenReturn(true);
        doNothing().when(countryRepository).deleteById(1);

        // Act
        countryService.deleteCountry(1);

        // Assert
        verify(countryRepository, times(1)).existsById(1);
        verify(countryRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("deleteCountry() should throw CountryNotFound when country does not exist")
    void deleteCountry_ShouldThrowCountryNotFound_WhenCountryDoesNotExist() {
        // Arrange
        when(countryRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        CountryNotFound exception = assertThrows(CountryNotFound.class,
                () -> countryService.deleteCountry(999));
        assertEquals("Country with ID 999 not found", exception.getMessage());
        verify(countryRepository, times(1)).existsById(999);
        verify(countryRepository, never()).deleteById(any());
    }
}
