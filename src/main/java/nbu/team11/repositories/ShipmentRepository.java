package nbu.team11.repositories;

import nbu.team11.entities.Shipment;
import nbu.team11.entities.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {
    List<Shipment> findAllByOrderByCreatedOnDesc();

    List<Shipment> findAllByEmployeeId(Integer employeeId);

    Shipment findByUniqueId(String uniqueId);

    @Query("SELECT s FROM Shipment s WHERE s.id NOT IN (SELECT ss.shipment.id FROM ShipmentStatus ss WHERE ss.status = :deliveredStatus)")
    List<Shipment> findUndeliveredShipments(@Param("deliveredStatus") Status deliveredStatus);

    List<Shipment> findBySenderClientId(Integer clientId);

    List<Shipment> findByRecipientClientId(Integer clientId);

    @Query("SELECT s FROM Shipment s WHERE (s.senderClient.id = :clientId OR s.recipientClient.id = :clientId) " +
           "AND s.id NOT IN (SELECT ss.shipment.id FROM ShipmentStatus ss WHERE ss.status = :deliveredStatus)")
    List<Shipment> findActiveShipmentsByClientId(@Param("clientId") Integer clientId, @Param("deliveredStatus") Status deliveredStatus);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Shipment s " +
           "WHERE s.senderAddress.id = :addressId OR s.recipientAddress.id = :addressId")
    boolean existsByAddressId(@Param("addressId") Integer addressId);

    @Query("SELECT s FROM Shipment s " +
           "LEFT JOIN s.senderClient sc LEFT JOIN sc.user su " +
           "LEFT JOIN s.recipientClient rc LEFT JOIN rc.user ru " +
           "LEFT JOIN s.receivingOffice ro " +
           "WHERE (:search IS NULL OR :search = '' OR " +
           "  LOWER(CONCAT(su.firstName, ' ', su.lastName)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "  LOWER(CONCAT(ru.firstName, ' ', ru.lastName)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "  LOWER(ro.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR " +
           "  (SELECT ss.status FROM ShipmentStatus ss WHERE ss.shipment = s AND ss.createdOn = " +
           "    (SELECT MAX(ss2.createdOn) FROM ShipmentStatus ss2 WHERE ss2.shipment = s)) = :status) " +
           "ORDER BY s.createdOn DESC")
    List<Shipment> searchShipments(@Param("search") String search, @Param("status") Status status);
}