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

    // create a city
    public City createCity(City city) {
        return cityRepository.save(city);
    }

    // all of the cities
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    // id of a city
    public Optional<City> getCityById(Integer id) {
        return cityRepository.findById(id);
    }

    // modify a city
    public City updateCity(Integer id, City updatedCity) {
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