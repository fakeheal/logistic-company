package nbu.team11.controllers;

import nbu.team11.dtos.CityDto;
import nbu.team11.services.CityService;
import nbu.team11.services.exceptions.CityNotFound;
import nbu.team11.services.exceptions.CountryNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing cities in the system.
 * Provides CRUD operations and error handling for cities.
 */
@RestController
@RequestMapping("/cities")
public class CityController {

    @Autowired
    private CityService cityService;

    /**
     * Retrieves a list of all cities.
     *
     * @return A list of {@link CityDto} objects representing all cities.
     */
    @GetMapping
    public List<CityDto> getAllCities() {
        return cityService.getAllCities();
    }

    /**
     * Retrieves a city by its ID.
     *
     * @param id The ID of the city to retrieve.
     * @return A {@link CityDto} representing the requested city.
     */
    @GetMapping("/{id}")
    public CityDto getCityById(@PathVariable Integer id) {
        return cityService.getCityById(id);
    }

    /**
     * Creates a new city.
     *
     * @param cityDto The details of the city to create.
     * @return The created {@link CityDto}.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CityDto createCity(@RequestBody CityDto cityDto) {
        return cityService.createCity(cityDto);
    }

    /**
     * Updates an existing city by its ID.
     *
     * @param id      The ID of the city to update.
     * @param cityDto The updated details of the city.
     * @return The updated {@link CityDto}.
     */
    @PutMapping("/{id}")
    public CityDto updateCity(@PathVariable Integer id, @RequestBody CityDto cityDto) {
        return cityService.updateCity(id, cityDto);
    }

    /**
     * Deletes a city by its ID.
     *
     * @param id The ID of the city to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCity(@PathVariable Integer id) {
        cityService.deleteCity(id);
    }

    /**
     * Handles {@link CityNotFound} exceptions.
     *
     * @param exception The exception that was thrown.
     * @return The error message from the exception.
     */
    @ExceptionHandler(CityNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCityNotFound(CityNotFound exception) {
        return exception.getMessage();
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
