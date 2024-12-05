package nbu.team11.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import nbu.team11.entities.Country;
import nbu.team11.repositories.CountryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    // Create new country
    public Country createCountry(Country country) {
        return countryRepository.save(country);
    }

    // get all of the countries
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    // returns a country by id
    public Optional<Country> getCountryById(Integer id) {
        return countryRepository.findById(id);
    }

    // actualizing the existing country
    public Country updateCountry(Integer id, Country updatedCountry) {
        if (countryRepository.existsById(id)) {
            updatedCountry.setId(id);
            return countryRepository.save(updatedCountry);
        }
        return null;
    }

    // delete a country
    public void deleteCountry(Integer id) {
        countryRepository.deleteById(id);
    }
}