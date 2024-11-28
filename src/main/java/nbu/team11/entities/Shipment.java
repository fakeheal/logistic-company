package nbu.team11.entities;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "EmployeeId", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "RecipientId", nullable = false)
    private Client recipient;

    @ManyToOne
    @JoinColumn(name = "RecipientAddress", nullable = false)
    private Address recipientAddress;

    @ManyToOne
    @JoinColumn(name = "SenderId", nullable = false)
    private Client sender;

    @ManyToOne
    @JoinColumn(name = "SenderAddress", nullable = false)
    private Address senderAddress;

    @ManyToOne
    @JoinColumn(name = "OfficeId", nullable = false)
    private Office office;

    @Column(name = "Weight", nullable = false)
    private double weight;

    @Column(name = "Price", nullable = false)
    private BigDecimal price;
}
