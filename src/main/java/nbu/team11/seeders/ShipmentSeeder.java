package nbu.team11.seeders;

import nbu.team11.entities.*;
import nbu.team11.entities.enums.Status;
import nbu.team11.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
@Order(4)
public class ShipmentSeeder implements CommandLineRunner {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentStatusRepository shipmentStatusRepository;
    private final ClientRepository clientRepository;
    private final OfficeRepository officeRepository;
    private final EntityManager entityManager;

    public ShipmentSeeder(
            ShipmentRepository shipmentRepository,
            ShipmentStatusRepository shipmentStatusRepository,
            ClientRepository clientRepository,
            OfficeRepository officeRepository,
            EntityManager entityManager) {

        this.shipmentRepository = shipmentRepository;
        this.shipmentStatusRepository = shipmentStatusRepository;
        this.clientRepository = clientRepository;
        this.officeRepository = officeRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(String... args) {

        if (shipmentRepository.count() > 0) return;

        Client sender = clientRepository.findById(1).orElseThrow();
        Client recipient = clientRepository.findById(2).orElseThrow();
        Office office = officeRepository.findAll().get(0);

        Address senderAddress = sender.getAddresses().get(0);

        // ======================
        // SHIPMENT
        // ======================
        Shipment shipment = new Shipment();
        shipment.setSenderClient(sender);
        shipment.setRecipientClient(recipient);
        shipment.setSenderAddress(senderAddress);
        shipment.setReceivingOffice(office);
        shipment.setDeliverToOffice(true);
        shipment.setTakeFromClientAddress(true);
        shipment.setWeight(4.2);
        shipment.setPrice(new BigDecimal("14.50"));
        shipment.setPayed(true);
        shipment.setUniqueId(UUID.randomUUID().toString());

        shipmentRepository.save(shipment);

        // ======================
        // STATUS HISTORY
        // ======================
        Instant now = Instant.now();

        createStatus(shipment, Status.SUBMITTED, now.minus(6, ChronoUnit.DAYS));
        createStatus(shipment, Status.PROCESSED, now.minus(5, ChronoUnit.DAYS));
        createStatus(shipment, Status.SHIPPED, now.minus(4, ChronoUnit.DAYS));
        createStatus(shipment, Status.IN_TRANSIT, now.minus(3, ChronoUnit.DAYS));
        createStatus(shipment, Status.OUT_FOR_DELIVERY, now.minus(1, ChronoUnit.DAYS));
        createStatus(shipment, Status.DELIVERED, now.minus(2, ChronoUnit.HOURS));
    }

    private void createStatus(Shipment shipment, Status status, Instant createdOn) {

        ShipmentStatus shipmentStatus = new ShipmentStatus();
        shipmentStatus.setShipment(shipment);
        shipmentStatus.setStatus(status);
        shipmentStatus.setComment("Status changed to " + status);

        shipmentStatusRepository.save(shipmentStatus);

        // Override @CreationTimestamp safely
        entityManager.createQuery("""
            UPDATE ShipmentStatus s
            SET s.createdOn = :createdOn
            WHERE s.id = :id
        """)
                .setParameter("createdOn", createdOn)
                .setParameter("id", shipmentStatus.getId())
                .executeUpdate();
    }
}
