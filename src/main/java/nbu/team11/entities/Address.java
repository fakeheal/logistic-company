package nbu.team11.entities;

import jakarta.persistence.*;
import lombok.Data;
import nbu.team11.entities.enums.AddressType;


@Entity
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "CityId", nullable = false)
    private City city;

    @Column(name = "Street", nullable = false)
    private String street;

    @Column(name = "PostalCode", nullable = false)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "AddressType", nullable = false)
    private AddressType addressType;
}
