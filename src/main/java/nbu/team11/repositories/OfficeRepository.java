package nbu.team11.repositories;

import nbu.team11.entities.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//TODO: Rewrite find by city and find by country
@Repository
public interface OfficeRepository extends JpaRepository<Office, Integer> {
    Office findByTitle(String title);
    //List<Office> findByCity(Integer cityId);
    //List<Office> findByCountry(Integer countryId);
}