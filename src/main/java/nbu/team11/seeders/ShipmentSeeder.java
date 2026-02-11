package nbu.team11.seeders;

import nbu.team11.entities.*;
import nbu.team11.entities.enums.PositionType;
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
import java.util.List;
import java.util.UUID;

@Component
@Order(4)
public class ShipmentSeeder implements CommandLineRunner {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentStatusRepository shipmentStatusRepository;
    private final ClientRepository clientRepository;
    private final OfficeRepository officeRepository;
    private final EmployeeRepository employeeRepository;
    private final EntityManager entityManager;

    public ShipmentSeeder(
            ShipmentRepository shipmentRepository,
            ShipmentStatusRepository shipmentStatusRepository,
            ClientRepository clientRepository,
            OfficeRepository officeRepository,
            EmployeeRepository employeeRepository,
            EntityManager entityManager) {

        this.shipmentRepository = shipmentRepository;
        this.shipmentStatusRepository = shipmentStatusRepository;
        this.clientRepository = clientRepository;
        this.officeRepository = officeRepository;
        this.employeeRepository = employeeRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(String... args) {

        if (shipmentRepository.count() > 0) return;

        List<Client> clients = clientRepository.findAll();
        List<Office> offices = officeRepository.findAll();
        List<Employee> employees = employeeRepository.findAll();

        if (clients.size() < 4 || offices.isEmpty() || employees.isEmpty()) return;

        Client petar = clients.get(0);
        Client anna = clients.get(1);
        Client dimitar = clients.get(2);
        Client silvia = clients.get(3);

        Office sofia = offices.get(0);
        Office plovdiv = offices.size() > 1 ? offices.get(1) : sofia;
        Office varna = offices.size() > 2 ? offices.get(2) : sofia;

        Employee coordinator = employees.stream()
                .filter(e -> e.getPositionType() == PositionType.COORDINATOR)
                .findFirst().orElse(employees.get(0));
        Employee deliveryman = employees.stream()
                .filter(e -> e.getPositionType() == PositionType.DELIVERYMAN)
                .findFirst().orElse(employees.get(0));

        Instant now = Instant.now();

        // ========================================================
        // 1. DELIVERED - Petar -> Anna, to office, paid, assigned
        // ========================================================
        Shipment s1 = buildShipment(petar, anna,
                petar.getAddresses().get(0), null,
                null, sofia,
                4.2, "14.50", true, coordinator);
        shipmentRepository.save(s1);
        createStatus(s1, Status.SUBMITTED, now.minus(10, ChronoUnit.DAYS));
        createStatus(s1, Status.SUBMITTED_PAID, now.minus(10, ChronoUnit.DAYS).plus(1, ChronoUnit.HOURS));
        createStatus(s1, Status.PROCESSED, now.minus(9, ChronoUnit.DAYS));
        createStatus(s1, Status.SHIPPED, now.minus(8, ChronoUnit.DAYS));
        createStatus(s1, Status.IN_TRANSIT, now.minus(7, ChronoUnit.DAYS));
        createStatus(s1, Status.OUT_FOR_DELIVERY, now.minus(6, ChronoUnit.DAYS));
        createStatus(s1, Status.DELIVERED, now.minus(5, ChronoUnit.DAYS));

        // ========================================================
        // 2. OUT FOR DELIVERY - Anna -> Dimitar, to address, paid, assigned to deliveryman
        // ========================================================
        Shipment s2 = buildShipment(anna, dimitar,
                anna.getAddresses().get(0), dimitar.getAddresses().get(0),
                null, null,
                1.8, "8.90", true, deliveryman);
        shipmentRepository.save(s2);
        createStatus(s2, Status.SUBMITTED, now.minus(4, ChronoUnit.DAYS));
        createStatus(s2, Status.SUBMITTED_PAID, now.minus(4, ChronoUnit.DAYS).plus(30, ChronoUnit.MINUTES));
        createStatus(s2, Status.PROCESSED, now.minus(3, ChronoUnit.DAYS));
        createStatus(s2, Status.SHIPPED, now.minus(2, ChronoUnit.DAYS));
        createStatus(s2, Status.IN_TRANSIT, now.minus(1, ChronoUnit.DAYS));
        createStatus(s2, Status.OUT_FOR_DELIVERY, now.minus(4, ChronoUnit.HOURS));

        // ========================================================
        // 3. IN TRANSIT - Dimitar -> Silvia, to office (Varna), paid, assigned
        // ========================================================
        Shipment s3 = buildShipment(dimitar, silvia,
                dimitar.getAddresses().get(0), null,
                plovdiv, varna,
                12.5, "32.00", true, coordinator);
        shipmentRepository.save(s3);
        createStatus(s3, Status.SUBMITTED, now.minus(5, ChronoUnit.DAYS));
        createStatus(s3, Status.SUBMITTED_PAID, now.minus(5, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
        createStatus(s3, Status.PROCESSED, now.minus(4, ChronoUnit.DAYS));
        createStatus(s3, Status.SHIPPED, now.minus(3, ChronoUnit.DAYS));
        createStatus(s3, Status.IN_TRANSIT, now.minus(2, ChronoUnit.DAYS));

        // ========================================================
        // 4. SHIPPED - Silvia -> Petar, to office (Sofia), paid, assigned
        // ========================================================
        Shipment s4 = buildShipment(silvia, petar,
                silvia.getAddresses().get(0), null,
                varna, sofia,
                0.5, "5.20", true, deliveryman);
        shipmentRepository.save(s4);
        createStatus(s4, Status.SUBMITTED, now.minus(3, ChronoUnit.DAYS));
        createStatus(s4, Status.SUBMITTED_PAID, now.minus(3, ChronoUnit.DAYS).plus(15, ChronoUnit.MINUTES));
        createStatus(s4, Status.PROCESSED, now.minus(2, ChronoUnit.DAYS));
        createStatus(s4, Status.SHIPPED, now.minus(1, ChronoUnit.DAYS));

        // ========================================================
        // 5. PROCESSED - Petar -> Dimitar, to address, paid, unassigned
        // ========================================================
        Shipment s5 = buildShipment(petar, dimitar,
                petar.getAddresses().get(1), dimitar.getAddresses().get(0),
                null, null,
                3.0, "11.50", true, null);
        shipmentRepository.save(s5);
        createStatus(s5, Status.SUBMITTED, now.minus(2, ChronoUnit.DAYS));
        createStatus(s5, Status.SUBMITTED_PAID, now.minus(2, ChronoUnit.DAYS).plus(1, ChronoUnit.HOURS));
        createStatus(s5, Status.PROCESSED, now.minus(1, ChronoUnit.DAYS));

        // ========================================================
        // 6. SUBMITTED_PAID - Anna -> Silvia, to office (Plovdiv), paid, unassigned
        // ========================================================
        Shipment s6 = buildShipment(anna, silvia,
                anna.getAddresses().get(1), null,
                null, plovdiv,
                7.3, "22.80", true, null);
        shipmentRepository.save(s6);
        createStatus(s6, Status.SUBMITTED, now.minus(1, ChronoUnit.DAYS));
        createStatus(s6, Status.SUBMITTED_PAID, now.minus(20, ChronoUnit.HOURS));

        // ========================================================
        // 7. SUBMITTED (not paid) - Dimitar -> Anna, to office (Sofia), not paid, unassigned
        // ========================================================
        Shipment s7 = buildShipment(dimitar, anna,
                dimitar.getAddresses().get(0), null,
                null, sofia,
                2.1, "9.40", false, null);
        shipmentRepository.save(s7);
        createStatus(s7, Status.SUBMITTED, now.minus(12, ChronoUnit.HOURS));

        // ========================================================
        // 8. SUBMITTED (not paid) - Silvia -> Petar, to address, not paid, unassigned
        // ========================================================
        Shipment s8 = buildShipment(silvia, petar,
                silvia.getAddresses().get(1), petar.getAddresses().get(0),
                null, null,
                0.3, "4.10", false, null);
        shipmentRepository.save(s8);
        createStatus(s8, Status.SUBMITTED, now.minus(2, ChronoUnit.HOURS));
    }

    private Shipment buildShipment(Client sender, Client recipient,
                                   Address senderAddress, Address recipientAddress,
                                   Office sendingOffice, Office receivingOffice,
                                   double weight, String price, boolean paid, Employee employee) {
        Shipment shipment = new Shipment();
        shipment.setSenderClient(sender);
        shipment.setRecipientClient(recipient);
        shipment.setSenderAddress(senderAddress);
        shipment.setRecipientAddress(recipientAddress);
        shipment.setSendingOffice(sendingOffice);
        shipment.setReceivingOffice(receivingOffice);
        shipment.setDeliverToOffice(receivingOffice != null);
        shipment.setTakeFromClientAddress(senderAddress != null);
        shipment.setWeight(weight);
        shipment.setPrice(new BigDecimal(price));
        shipment.setPayed(paid);
        shipment.setUniqueId(UUID.randomUUID().toString());
        shipment.setEmployee(employee);
        return shipment;
    }

    private void createStatus(Shipment shipment, Status status, Instant createdOn) {
        ShipmentStatus shipmentStatus = new ShipmentStatus();
        shipmentStatus.setShipment(shipment);
        shipmentStatus.setStatus(status);
        shipmentStatus.setComment("Status changed to " + status.name().replace("_", " ").toLowerCase());

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
