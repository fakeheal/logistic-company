package nbu.team11.services;

import nbu.team11.dtos.CountryDto;
import nbu.team11.entities.Country;
import nbu.team11.repositories.CountryRepository;
import nbu.team11.services.CountryService;
import nbu.team11.services.exceptions.CountryNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CountryService countryService;

    private Country country;
    private CountryDto countryDto;
    private Country existingCountry;
    private CountryDto duplicateCountryDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        country = new Country();
        country.setId(1);
        country.setName("Bulgaria");

        countryDto = new CountryDto();
        countryDto.setId(1);
        countryDto.setName("Bulgaria");

        existingCountry = new Country();
        existingCountry.setId(1);
        existingCountry.setName("Bulgaria");

        duplicateCountryDto = new CountryDto();
        duplicateCountryDto.setName("Bulgaria");
    }

    @Test
    void testGetAllCountries() {
        when(countryRepository.findAll()).thenReturn(List.of(country));
        when(modelMapper.map(country, CountryDto.class)).thenReturn(countryDto);

        List<CountryDto> countries = countryService.getAllCountries();

        assertNotNull(countries);
        assertEquals(1, countries.size());
        assertEquals("Bulgaria", countries.get(0).getName());
    }

    @Test
    void testGetCountryById() {
        when(countryRepository.findById(1)).thenReturn(Optional.of(country));
        when(modelMapper.map(country, CountryDto.class)).thenReturn(countryDto);

        CountryDto result = countryService.getCountryById(1);

        assertNotNull(result);
        assertEquals("Bulgaria", result.getName());
    }

    @Test
    void testGetCountryById_CountryNotFound() {
        when(countryRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(CountryNotFound.class, () -> countryService.getCountryById(2));
    }

    @Test
    void testCreateCountry() {
        when(countryRepository.existsByName("Bulgaria")).thenReturn(false);
        when(countryRepository.save(any(Country.class))).thenReturn(country);
        when(modelMapper.map(any(Country.class), eq(CountryDto.class))).thenReturn(countryDto);

        CountryDto result = countryService.createCountry(countryDto);

        assertNotNull(result);
        assertEquals("Bulgaria", result.getName());
    }

    @Test
    void testCreateCountry_DuplicateName() {
        when(countryRepository.existsByName("Bulgaria")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> countryService.createCountry(countryDto));
    }

    @Test
    void testUpdateCountry() {
        Country updatedCountry = new Country();
        updatedCountry.setId(1);
        updatedCountry.setName("New Bulgaria");

        CountryDto updatedDto = new CountryDto();
        updatedDto.setId(1);
        updatedDto.setName("New Bulgaria");

        when(countryRepository.findById(1)).thenReturn(Optional.of(country));
        when(countryRepository.existsByName("New Bulgaria")).thenReturn(false);
        when(countryRepository.save(any(Country.class))).thenReturn(updatedCountry);
        when(modelMapper.map(any(Country.class), eq(CountryDto.class))).thenReturn(updatedDto);

        CountryDto result = countryService.updateCountry(1, updatedDto);

        assertNotNull(result);
        assertEquals("New Bulgaria", result.getName());
    }

    @Test
    void testUpdateCountry_CountryNotFound() {
        when(countryRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(CountryNotFound.class, () -> countryService.updateCountry(2, countryDto));
    }

    @Test
    void testUpdateCountry_DuplicateName() {
        Country existingCountry = new Country();
        existingCountry.setId(1);
        existingCountry.setName("Bulgaria");
        CountryDto duplicateCountryDto = new CountryDto();
        duplicateCountryDto.setId(2);
        duplicateCountryDto.setName("Bulgaria");
        when(countryRepository.findById(2)).thenReturn(Optional.of(existingCountry));
        when(countryRepository.existsByName("Bulgaria")).thenReturn(true);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            countryService.updateCountry(2, duplicateCountryDto);
        });
        assertEquals("A country with this name already exists.", exception.getMessage());
        verify(countryRepository, times(1)).findById(2);
        verify(countryRepository, times(1)).existsByName("Bulgaria");
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    void testDeleteCountry() {
        when(countryRepository.existsById(1)).thenReturn(true);
        doNothing().when(countryRepository).deleteById(1);

        assertDoesNotThrow(() -> countryService.deleteCountry(1));

        verify(countryRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteCountry_CountryNotFound() {
        when(countryRepository.existsById(2)).thenReturn(false);

        assertThrows(CountryNotFound.class, () -> countryService.deleteCountry(2));
    }
}
