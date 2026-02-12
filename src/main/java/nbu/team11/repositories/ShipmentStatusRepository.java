package nbu.team11.repositories;

import nbu.team11.entities.Shipment;
import nbu.team11.entities.ShipmentStatus;
import nbu.team11.entities.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ShipmentStatusRepository extends JpaRepository<ShipmentStatus, Integer> {
    List<ShipmentStatus> findByShipmentId(Integer shipmentId);
    List<ShipmentStatus> findByShipmentIdOrderByCreatedOnDesc(Integer shipmentId);
    List<ShipmentStatus> findByShipmentIdOrderByCreatedOnAsc(Integer shipmentId);

    @Query("SELECT ss.shipment FROM ShipmentStatus ss WHERE ss.status = :status AND ss.createdOn BETWEEN :from AND :to")
    List<Shipment> findShipmentsByStatusAndDateRange(
            @Param("status") Status status,
            @Param("from") Instant from,
            @Param("to") Instant to
    );
}