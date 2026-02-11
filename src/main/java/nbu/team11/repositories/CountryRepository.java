package nbu.team11.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import nbu.team11.entities.Country;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    Boolean existsByName(String name);
    Optional<Country> findByName(String name);
}