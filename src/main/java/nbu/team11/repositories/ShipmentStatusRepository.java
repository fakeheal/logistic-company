package nbu.team11.repositories;

import nbu.team11.entities.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentStatusRepository extends JpaRepository<ShipmentStatus, Integer> {
    List<ShipmentStatus> findByShipmentId(Integer shipmentId);

}