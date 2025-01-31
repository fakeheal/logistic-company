package nbu.team11.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import nbu.team11.entities.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    boolean existsByName(String name);
}