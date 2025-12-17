package nbu.team11.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Data
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "DeliverToOffice")
    private boolean deliverToOffice;

    @JoinColumn(name = "TakeFromClientAddress")
    private boolean takeFromClientAddress;

    //Employee id may be null if client submits the shipment by himself
    @ManyToOne
    @JoinColumn(name = "EmployeeId")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "RecipientId", nullable = false)
    private Client recipientClient;

    @ManyToOne
    @JoinColumn(name = "RecipientAddress")
    private Address recipientAddress;

    @ManyToOne
    @JoinColumn(name = "SenderId", nullable = false)
    private Client senderClient;

    @ManyToOne
    @JoinColumn(name = "SenderAddress")
    private Address senderAddress;

    @ManyToOne
    @JoinColumn(name = "SendingOfficeId")
    private Office sendingOffice;

    @ManyToOne
    @JoinColumn(name = "ReceivingOfficeId")
    private Office receivingOffice;

    @Positive(message = "The weight must be greater than 0")
    @Column(name = "Weight", nullable = false)
    private double weight;

    @Positive(message = "The price must be greater than 0")
    @Column(name = "Price", nullable = false)
    private BigDecimal price;

    @Column(name = "Payed", nullable = false)
    private boolean payed;

    @NotBlank(message = "Unique ID cannot be blank!")
    @Size(message = "Unique id has to be between 5 and 30 characters!")
    @Column(name = "uniqueId", nullable = false, unique = true)
    private String uniqueId;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private Instant createdOn;
    @UpdateTimestamp
    @Column(name = "updated_on", nullable = false)
    private Instant updatedOn;

    public Shipment() {
    }

    public Shipment(Employee employee, Client recipient, Address recipientAddress, Client sender, Address senderAddress,
            Office sendingOffice, Office receivingOffice, double weight, BigDecimal price) {
        this.employee = employee;
        this.recipientClient = recipient;
        this.recipientAddress = recipientAddress;
        this.senderClient = sender;
        this.senderAddress = senderAddress;
        this.sendingOffice = sendingOffice;
        this.receivingOffice = receivingOffice;
        this.weight = weight;
        this.price = price;
    }

}
