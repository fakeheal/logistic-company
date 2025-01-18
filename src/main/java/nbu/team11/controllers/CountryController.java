package nbu.team11.controllers;

import nbu.team11.entities.Country;
import nbu.team11.services.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/countries")
public class CountryController {

    @Autowired
    private CountryService countryService;

    // Creates a new country
    @PostMapping
    public ResponseEntity<Country> createCountry(@RequestBody Country country) {
        try {
            Country savedCountry = countryService.createCountry(country);
            return new ResponseEntity<>(savedCountry, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    // Returns all cities
    @GetMapping
    public List<Country> getAllCountries() {
        return countryService.getAllCountries();
    }

    // Returns a country by id
    @GetMapping("/{id}")
    public ResponseEntity<Country> getCountryById(@PathVariable Integer id) {
        Optional<Country> country = countryService.getCountryById(id);
        return country.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Actualizing a country
    @PutMapping("/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable Integer id, @RequestBody Country country) {
        Country updatedCountry = countryService.updateCountry(id, country);
        return updatedCountry != null ? ResponseEntity.ok(updatedCountry) : ResponseEntity.notFound().build();
    }

    // Deletes a country
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Integer id) {
        countryService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }
}