package nbu.team11.repositories;

import nbu.team11.entities.Address;
import nbu.team11.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//TODO: Write find by country explicitly
@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByPostalCodeAndStreetAndCity_Country_Name(String postalCode, String street, String country);
    Optional<Address> findByStreet(String street);
    //List<Address> findByCountry(String country);
    List<Address> findByCity(City city);
}

