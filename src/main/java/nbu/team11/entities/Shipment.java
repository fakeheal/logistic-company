package nbu.team11.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data
public class Shipment {
    @Setter
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

    @Positive(message = "The weight must be greater than 0")
    @Column(name = "Weight", nullable = false)
    private double weight;

    @Positive(message = "The price must be greater than 0")
    @Column(name = "Price", nullable = false)
    private BigDecimal price;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private Instant createdOn;
    @UpdateTimestamp
    @Column(name = "updated_on", nullable = false)
    private Instant updatedOn;

    public Shipment(Employee employee, Client recipient, Address recipientAddress, Client sender, Address senderAddress,
            Office office, double weight, BigDecimal price) {
        this.employee = employee;
        this.recipient = recipient;
        this.recipientAddress = recipientAddress;
        this.sender = sender;
        this.senderAddress = senderAddress;
        this.office = office;
        this.weight = weight;
        this.price = price;
    }

}
