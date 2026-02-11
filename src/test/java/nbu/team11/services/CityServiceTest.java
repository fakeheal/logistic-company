package nbu.team11.services;

import nbu.team11.dtos.CityDto;
import nbu.team11.entities.City;
import nbu.team11.entities.Country;
import nbu.team11.repositories.CityRepository;
import nbu.team11.repositories.CountryRepository;
import nbu.team11.services.exceptions.CityNotFound;
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
@DisplayName("CityService Tests")
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CityService cityService;

    private Country testCountry;
    private City testCity;
    private CityDto testCityDto;

    @BeforeEach
    void setUp() {
        testCountry = Country.builder()
                .id(1)
                .name("Bulgaria")
                .createdOn(Instant.now())
                .updatedOn(Instant.now())
                .build();

        testCity = City.builder()
                .id(1)
                .name("Sofia")
                .country(testCountry)
                .createdOn(Instant.now())
                .updatedOn(Instant.now())
                .build();

        testCityDto = new CityDto();
        testCityDto.setId(1);
        testCityDto.setName("Sofia");
        testCityDto.setCountryId(1);
        testCityDto.setCreatedOn(Instant.now());
        testCityDto.setUpdatedOn(Instant.now());
    }

    @Test
    @DisplayName("getAllCities() should return list of all cities as DTOs")
    void getAllCities_ShouldReturnListOfCityDtos() {
        // Arrange
        City city2 = City.builder()
                .id(2)
                .name("Plovdiv")
                .country(testCountry)
                .build();

        CityDto cityDto2 = new CityDto();
        cityDto2.setId(2);
        cityDto2.setName("Plovdiv");
        cityDto2.setCountryId(1);

        List<City> cities = Arrays.asList(testCity, city2);

        when(cityRepository.findAll()).thenReturn(cities);
        when(modelMapper.map(testCity, CityDto.class)).thenReturn(testCityDto);
        when(modelMapper.map(city2, CityDto.class)).thenReturn(cityDto2);

        // Act
        List<CityDto> result = cityService.getAllCities();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Sofia", result.get(0).getName());
        assertEquals("Plovdiv", result.get(1).getName());
    }

    @Test
    @DisplayName("getAllCities() should return empty list when no cities exist")
    void getAllCities_ShouldReturnEmptyList_WhenNoCitiesExist() {
        // Arrange
        when(cityRepository.findAll()).thenReturn(List.of());

        // Act
        List<CityDto> result = cityService.getAllCities();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getCityById() should return city DTO when city exists")
    void getCityById_ShouldReturnCityDto_WhenCityExists() {
        // Arrange
        when(cityRepository.findById(1)).thenReturn(Optional.of(testCity));
        when(modelMapper.map(testCity, CityDto.class)).thenReturn(testCityDto);

        // Act
        CityDto result = cityService.getCityById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Sofia", result.getName());
        assertEquals(1, result.getCountryId());
    }

    @Test
    @DisplayName("getCityById() should throw CityNotFound when city does not exist")
    void getCityById_ShouldThrowCityNotFound_WhenCityDoesNotExist() {
        // Arrange
        when(cityRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        CityNotFound exception = assertThrows(CityNotFound.class,
                () -> cityService.getCityById(999));
        assertEquals("City with ID 999 not found", exception.getMessage());
    }

    @Test
    @DisplayName("createCity() should create new city successfully")
    void createCity_ShouldCreateNewCitySuccessfully() {
        // Arrange
        CityDto newCityDto = new CityDto();
        newCityDto.setName("Varna");
        newCityDto.setCountryId(1);

        City savedCity = City.builder()
                .id(3)
                .name("Varna")
                .country(testCountry)
                .build();

        CityDto expectedDto = new CityDto();
        expectedDto.setId(3);
        expectedDto.setName("Varna");
        expectedDto.setCountryId(1);

        when(countryRepository.findById(1)).thenReturn(Optional.of(testCountry));
        when(cityRepository.save(any(City.class))).thenReturn(savedCity);
        when(modelMapper.map(savedCity, CityDto.class)).thenReturn(expectedDto);

        // Act
        CityDto result = cityService.createCity(newCityDto);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getId());
        assertEquals("Varna", result.getName());
        assertEquals(1, result.getCountryId());
    }

    @Test
    @DisplayName("createCity() should throw CountryNotFound when country does not exist")
    void createCity_ShouldThrowCountryNotFound_WhenCountryDoesNotExist() {
        // Arrange
        CityDto newCityDto = new CityDto();
        newCityDto.setName("Varna");
        newCityDto.setCountryId(999);

        when(countryRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        CountryNotFound exception = assertThrows(CountryNotFound.class,
                () -> cityService.createCity(newCityDto));
        assertEquals("Country with ID 999 not found", exception.getMessage());
    }

    @Test
    @DisplayName("updateCity() should update existing city successfully")
    void updateCity_ShouldUpdateExistingCitySuccessfully() {
        // Arrange
        Integer cityId = 1;
        CityDto updateDto = new CityDto();
        updateDto.setName("Sofia Updated");
        updateDto.setCountryId(1);

        City updatedCity = City.builder()
                .id(cityId)
                .name("Sofia Updated")
                .country(testCountry)
                .build();

        CityDto expectedDto = new CityDto();
        expectedDto.setId(cityId);
        expectedDto.setName("Sofia Updated");
        expectedDto.setCountryId(1);

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(testCity));
        when(countryRepository.findById(1)).thenReturn(Optional.of(testCountry));
        when(cityRepository.save(testCity)).thenReturn(updatedCity);
        when(modelMapper.map(updatedCity, CityDto.class)).thenReturn(expectedDto);

        // Act
        CityDto result = cityService.updateCity(cityId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(cityId, result.getId());
        assertEquals("Sofia Updated", result.getName());
    }

    @Test
    @DisplayName("updateCity() should update city with different country")
    void updateCity_ShouldUpdateCityWithDifferentCountry() {
        // Arrange
        Integer cityId = 1;
        Country newCountry = Country.builder()
                .id(2)
                .name("Romania")
                .build();

        CityDto updateDto = new CityDto();
        updateDto.setName("Sofia");
        updateDto.setCountryId(2);

        City updatedCity = City.builder()
                .id(cityId)
                .name("Sofia")
                .country(newCountry)
                .build();

        CityDto expectedDto = new CityDto();
        expectedDto.setId(cityId);
        expectedDto.setName("Sofia");
        expectedDto.setCountryId(2);

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(testCity));
        when(countryRepository.findById(2)).thenReturn(Optional.of(newCountry));
        when(cityRepository.save(testCity)).thenReturn(updatedCity);
        when(modelMapper.map(updatedCity, CityDto.class)).thenReturn(expectedDto);

        // Act
        CityDto result = cityService.updateCity(cityId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getCountryId());
    }

    @Test
    @DisplayName("updateCity() should throw CityNotFound when city does not exist")
    void updateCity_ShouldThrowCityNotFound_WhenCityDoesNotExist() {
        // Arrange
        CityDto updateDto = new CityDto();
        updateDto.setName("NonExistent");
        updateDto.setCountryId(1);

        when(cityRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        CityNotFound exception = assertThrows(CityNotFound.class,
                () -> cityService.updateCity(999, updateDto));
        assertEquals("City with ID 999 not found", exception.getMessage());
    }

    @Test
    @DisplayName("updateCity() should throw CountryNotFound when new country does not exist")
    void updateCity_ShouldThrowCountryNotFound_WhenCountryDoesNotExist() {
        // Arrange
        Integer cityId = 1;
        CityDto updateDto = new CityDto();
        updateDto.setName("Sofia");
        updateDto.setCountryId(999);

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(testCity));
        when(countryRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        CountryNotFound exception = assertThrows(CountryNotFound.class,
                () -> cityService.updateCity(cityId, updateDto));
        assertEquals("Country with ID 999 not found", exception.getMessage());
    }

    @Test
    @DisplayName("deleteCity() should delete city successfully")
    void deleteCity_ShouldDeleteCitySuccessfully() {
        // Arrange
        when(cityRepository.existsById(1)).thenReturn(true);
        doNothing().when(cityRepository).deleteById(1);

        // Act
        cityService.deleteCity(1);

        // Assert
        verify(cityRepository, times(1)).existsById(1);
        verify(cityRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("deleteCity() should throw CityNotFound when city does not exist")
    void deleteCity_ShouldThrowCityNotFound_WhenCityDoesNotExist() {
        // Arrange
        when(cityRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        CityNotFound exception = assertThrows(CityNotFound.class,
                () -> cityService.deleteCity(999));
        assertEquals("City with ID 999 not found", exception.getMessage());
    }
}
