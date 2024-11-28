package nbu.team11.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Office {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "Title", nullable = false)
    private String title;

    @OneToOne
    @JoinColumn(name = "address_id",nullable = false)
    private Address address;
}
