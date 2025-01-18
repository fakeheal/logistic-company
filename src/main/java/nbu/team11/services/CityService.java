package nbu.team11.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import nbu.team11.entities.City;
import nbu.team11.repositories.CityRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {

    @Autowired
    private CityRepository cityRepository;


    // all the cities
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    // id of a city
    public Optional<City> getCityById(Integer id) {
        return cityRepository.findById(id);
    }

    public City createCity(City city) {
        if (cityRepository.existsByName(city.getName())) {
            throw new IllegalArgumentException("A city with this name already exists.");
        }
        return cityRepository.save(city);
    }

    public City updateCity(Integer id, City updatedCity) {
        if (cityRepository.existsByName(updatedCity.getName()) && !updatedCity.getId().equals(id)) {
            throw new IllegalArgumentException("A city with this name already exists.");
        }
        if (cityRepository.existsById(id)) {
            updatedCity.setId(id);
            return cityRepository.save(updatedCity);
        }
        return null;
    }

    // delete a city
    public void deleteCity(Integer id) {
        cityRepository.deleteById(id);
    }
}