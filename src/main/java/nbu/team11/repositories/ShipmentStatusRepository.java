package nbu.team11.repositories;

import nbu.team11.entities.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentStatusRepository extends JpaRepository<ShipmentStatus, Integer> {
    void deleteByShipmentId(Integer shipmentId);
}