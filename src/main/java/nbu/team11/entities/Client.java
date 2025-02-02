package nbu.team11.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
public class Client {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Getter
    @OneToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @Setter
    @Getter
    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private Instant createdOn;
    @UpdateTimestamp
    @Column(name = "updated_on", nullable = false)
    private Instant updatedOn;

}
