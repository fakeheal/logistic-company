
package nbu.team11.controllers;
import nbu.team11.entities.City;
import nbu.team11.services.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cities")
public class CityController {

    @Autowired
    private CityService cityService;

    // Creates a new city
    @PostMapping
    public ResponseEntity<City> createCity(@RequestBody City city) {
        City savedCity = cityService.createCity(city);
        return new ResponseEntity<>(savedCity, HttpStatus.CREATED);
    }

    // Returns all cities
    @GetMapping
    public List<City> getAllCities() {
        return cityService.getAllCities();
    }

    // Returns a city by id
    @GetMapping("/{id}")
    public ResponseEntity<City> getCityById(@PathVariable Integer id) {
        Optional<City> city = cityService.getCityById(id);
        return city.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<City> updateCity(@PathVariable Integer id, @RequestBody City city) {
        City updatedCity = cityService.updateCity(id, city);
        return updatedCity != null ? ResponseEntity.ok(updatedCity) : ResponseEntity.notFound().build();
    }

    // Deleting a city
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Integer id) {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }
}