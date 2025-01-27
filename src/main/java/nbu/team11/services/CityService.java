package nbu.team11.services;

import nbu.team11.dtos.CityDto;
import nbu.team11.entities.City;
import nbu.team11.entities.Country;
import nbu.team11.repositories.CityRepository;
import nbu.team11.repositories.CountryRepository;
import nbu.team11.services.exceptions.CityNotFound;
import nbu.team11.services.exceptions.CountryNotFound;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing cities in the system.
 * Provides methods for CRUD operations and integration with countries.
 */
@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Retrieves a list of all cities.
     *
     * @return A list of {@link CityDto} objects representing all cities.
     */
    public List<CityDto> getAllCities() {
        return cityRepository.findAll().stream()
                .map(city -> modelMapper.map(city, CityDto.class))
                .toList();
    }

    /**
     * Retrieves a city by its ID.
     *
     * @param id The ID of the city to retrieve.
     * @return A {@link CityDto} representing the requested city.
     * @throws CityNotFound if no city is found with the given ID.
     */
    public CityDto getCityById(Integer id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFound("City with ID " + id + " not found"));
        return modelMapper.map(city, CityDto.class);
    }

    /**
     * Creates a new city.
     *
     * @param cityDto The details of the city to create.
     * @return The created {@link CityDto}.
     * @throws CountryNotFound if the associated country does not exist.
     */
    public CityDto createCity(CityDto cityDto) {
        Country country = countryRepository.findById(cityDto.getCountryId())
                .orElseThrow(() -> new CountryNotFound("Country with ID " + cityDto.getCountryId() + " not found"));

        City city = new City();
        city.setName(cityDto.getName());
        city.setCountry(country);

        City savedCity = cityRepository.save(city);
        return modelMapper.map(savedCity, CityDto.class);
    }

    /**
     * Updates an existing city by its ID.
     *
     * @param id      The ID of the city to update.
     * @param cityDto The updated details of the city.
     * @return The updated {@link CityDto}.
     * @throws CityNotFound if no city is found with the given ID.
     * @throws CountryNotFound if the associated country does not exist.
     */
    public CityDto updateCity(Integer id, CityDto cityDto) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFound("City with ID " + id + " not found"));

        Country country = countryRepository.findById(cityDto.getCountryId())
                .orElseThrow(() -> new CountryNotFound("Country with ID " + cityDto.getCountryId() + " not found"));

        city.setName(cityDto.getName());
        city.setCountry(country);

        City updatedCity = cityRepository.save(city);
        return modelMapper.map(updatedCity, CityDto.class);
    }

    /**
     * Deletes a city by its ID.
     *
     * @param id The ID of the city to delete.
     * @throws CityNotFound if no city is found with the given ID.
     */
    public void deleteCity(Integer id) {
        if (!cityRepository.existsById(id)) {
            throw new CityNotFound("City with ID " + id + " not found");
        }
        cityRepository.deleteById(id);
    }
}
