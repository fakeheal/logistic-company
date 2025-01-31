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
public class Office {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Getter
    @Setter
    @Column(name = "Title", nullable = false)
    private String title;

    @Getter
    @OneToOne
    @JoinColumn(name = "address_id",nullable = false)
    private Address address;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private Instant createdOn;
    @UpdateTimestamp
    @Column(name = "updated_on", nullable = false)
    private Instant updatedOn;

    public void setAddress(Address address) {
        this.address = address;
    }
}
