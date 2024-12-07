package nbu.team11.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import nbu.team11.entities.City;

public interface CityRepository extends JpaRepository<City, Integer> {

}