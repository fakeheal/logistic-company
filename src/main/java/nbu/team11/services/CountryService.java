package nbu.team11.services;

import nbu.team11.dtos.CountryDto;
import nbu.team11.entities.Country;
import nbu.team11.repositories.CountryRepository;
import nbu.team11.services.exceptions.CountryNotFound;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing countries in the system.
 * Provides methods for CRUD operations and validation.
 */
@Service
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Retrieves a list of all countries.
     *
     * @return A list of {@link CountryDto} objects representing all countries.
     */
    public List<CountryDto> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(country -> modelMapper.map(country, CountryDto.class))
                .toList();
    }

    /**
     * Retrieves a country by its ID.
     *
     * @param id The ID of the country to retrieve.
     * @return A {@link CountryDto} representing the requested country.
     * @throws CountryNotFound if no country is found with the given ID.
     */
    public CountryDto getCountryById(Integer id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new CountryNotFound("Country with ID " + id + " not found"));
        return modelMapper.map(country, CountryDto.class);
    }

    /**
     * Creates a new country.
     *
     * @param countryDto The details of the country to create.
     * @return The created {@link CountryDto}.
     * @throws IllegalArgumentException if a country with the same name already
     *                                  exists.
     */
    public CountryDto createCountry(CountryDto countryDto) {
        if (countryRepository.existsByName(countryDto.getName())) {
            throw new IllegalArgumentException("A country with this name already exists.");
        }
        Country country = new Country();
        country.setName(countryDto.getName());
        Country savedCountry = countryRepository.save(country);
        return modelMapper.map(savedCountry, CountryDto.class);
    }

    /**
     * Updates an existing country by its ID.
     *
     * @param id         The ID of the country to update.
     * @param countryDto The updated details of the country.
     * @return The updated {@link CountryDto}.
     * @throws CountryNotFound          if no country is found with the given ID.
     * @throws IllegalArgumentException if a country with the same name already
     *                                  exists.
     */
    public CountryDto updateCountry(Integer id, CountryDto countryDto) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new CountryNotFound("Country with ID " + id + " not found"));

        if (countryRepository.existsByName(countryDto.getName()) && !country.getId().equals(id)) {
            throw new IllegalArgumentException("A country with this name already exists.");
        }

        country.setName(countryDto.getName());
        Country updatedCountry = countryRepository.save(country);
        return modelMapper.map(updatedCountry, CountryDto.class);
    }

    /**
     * Deletes a country by its ID.
     *
     * @param id The ID of the country to delete.
     * @throws CountryNotFound if no country is found with the given ID.
     */
    public void deleteCountry(Integer id) {
        if (!countryRepository.existsById(id)) {
            throw new CountryNotFound("Country with ID " + id + " not found");
        }
        countryRepository.deleteById(id);
    }
}
