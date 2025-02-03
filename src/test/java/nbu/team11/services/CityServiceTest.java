package nbu.team11.services;

import nbu.team11.dtos.CityDto;
import nbu.team11.entities.City;
import nbu.team11.entities.Country;
import nbu.team11.repositories.CityRepository;
import nbu.team11.repositories.CountryRepository;
import nbu.team11.services.exceptions.CityNotFound;
import nbu.team11.services.exceptions.CountryNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CityService cityService;

    private City city;
    private CityDto cityDto;
    private Country country;

    @BeforeEach
    void setUp() {
        country = new Country();
        country.setId(1);
        country.setName("Bulgaria");

        city = new City();
        city.setId(1);
        city.setName("Sofia");
        city.setCountry(country);

        cityDto = new CityDto();
        cityDto.setId(1);
        cityDto.setName("Sofia");
        cityDto.setCountryId(1);
    }

    @Test
    void testGetAllCities() {
        List<City> cities = Arrays.asList(city);
        when(cityRepository.findAll()).thenReturn(cities);
        when(modelMapper.map(city, CityDto.class)).thenReturn(cityDto);

        List<CityDto> cityDtos = cityService.getAllCities();

        assertEquals(1, cityDtos.size());
        assertEquals("Sofia", cityDtos.get(0).getName());
    }

    @Test
    void testGetCityById_Success() {
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(modelMapper.map(city, CityDto.class)).thenReturn(cityDto);

        CityDto foundCity = cityService.getCityById(1);

        assertNotNull(foundCity);
        assertEquals("Sofia", foundCity.getName());
    }

    @Test
    void testGetCityById_NotFound() {
        when(cityRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(CityNotFound.class, () -> cityService.getCityById(2));
    }

    @Test
    void testCreateCity_Success() {
        when(countryRepository.findById(1)).thenReturn(Optional.of(country));
        when(cityRepository.save(any(City.class))).thenReturn(city);
        when(modelMapper.map(city, CityDto.class)).thenReturn(cityDto);

        CityDto createdCity = cityService.createCity(cityDto);

        assertNotNull(createdCity);
        assertEquals("Sofia", createdCity.getName());
    }

    @Test
    void testCreateCity_CountryNotFound() {
        cityDto.setCountryId(2);
        when(countryRepository.findById(cityDto.getCountryId())).thenReturn(Optional.empty());

        assertThrows(CountryNotFound.class, () -> cityService.createCity(cityDto));
    }

    @Test
    void testUpdateCity_Success() {
        CityDto updatedCityDto = new CityDto();
        updatedCityDto.setName("Plovdiv");
        updatedCityDto.setCountryId(1);

        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(countryRepository.findById(1)).thenReturn(Optional.of(country));
        when(cityRepository.save(any(City.class))).thenReturn(city);
        when(modelMapper.map(city, CityDto.class)).thenReturn(updatedCityDto);

        CityDto updatedCity = cityService.updateCity(1, updatedCityDto);

        assertNotNull(updatedCity);
        assertEquals("Plovdiv", updatedCity.getName());
    }

    @Test
    void testUpdateCity_CityNotFound() {
        when(cityRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(CityNotFound.class, () -> cityService.updateCity(2, cityDto));
    }

    @Test
    void testUpdateCity_CountryNotFound() {
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(countryRepository.findById(2)).thenReturn(Optional.empty());

        cityDto.setCountryId(2);
        assertThrows(CountryNotFound.class, () -> cityService.updateCity(1, cityDto));
    }

    @Test
    void testDeleteCity_Success() {
        when(cityRepository.existsById(1)).thenReturn(true);
        doNothing().when(cityRepository).deleteById(1);

        assertDoesNotThrow(() -> cityService.deleteCity(1));

        verify(cityRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteCity_NotFound() {
        when(cityRepository.existsById(2)).thenReturn(false);

        assertThrows(CityNotFound.class, () -> cityService.deleteCity(2));
    }
}
