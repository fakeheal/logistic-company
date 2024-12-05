package nbu.team11.repositories;

import nbu.team11.entities.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
}
