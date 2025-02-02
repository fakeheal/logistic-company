package nbu.team11.controllers;

import nbu.team11.dtos.CountryDto;
import nbu.team11.services.CountryService;
import nbu.team11.services.exceptions.CountryNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Controller for managing countries in the system.
 * Provides CRUD operations and error handling for countries.
 */
@RestController
@RequestMapping("/countries")
public class CountryController {

    @Autowired
    private CountryService countryService;

    /**
     * Retrieves a list of all countries.
     *
     * @return A list of {@link CountryDto} objects representing all countries.
     */
    @GetMapping
    public List<CountryDto> getAllCountries() {
        return countryService.getAllCountries();
    }

    /**
     * Retrieves a country by its ID.
     *
     * @param id The ID of the country to retrieve.
     * @return A {@link CountryDto} representing the requested country.
     */
    @GetMapping("/{id}")
    public CountryDto getCountryById(@PathVariable Integer id) {
        return countryService.getCountryById(id);
    }

    /**
     * Creates a new country.
     *
     * @param countryDto The details of the country to create.
     * @return The created {@link CountryDto}.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CountryDto createCountry(@RequestBody CountryDto countryDto) {
        return countryService.createCountry(countryDto);
    }

    /**
     * Updates an existing country by its ID.
     *
     * @param id         The ID of the country to update.
     * @param countryDto The updated details of the country.
     * @return The updated {@link CountryDto}.
     */
    @PutMapping("/{id}")
    public CountryDto updateCountry(@PathVariable Integer id, @RequestBody CountryDto countryDto) {
        return countryService.updateCountry(id, countryDto);
    }

    /**
     * Deletes a country by its ID.
     *
     * @param id The ID of the country to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCountry(@PathVariable Integer id) {
        countryService.deleteCountry(id);
    }

    /**
     * Handles {@link CountryNotFound} exceptions.
     *
     * @param exception The exception that was thrown.
     * @return The error message from the exception.
     */
    @ExceptionHandler(CountryNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCountryNotFound(CountryNotFound exception) {
        return exception.getMessage();
    }
}
