package nbu.team11.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
public class Address {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "CityId", nullable = false)
    private City city;

    @Getter
    @Setter
    @NotBlank(message = "Street cannot be blank!")
    @Size(min = 5, max = 20, message = "Street name has to be between 5 and 20 characters!")
    @Column(name = "Street", nullable = false)
    private String street;

    @Getter
    @Setter
    @NotBlank(message = "Postal code cannot be blank!")
    @Column(name = "PostalCode", nullable = false)
    private String postalCode;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private Instant createdOn;
    @UpdateTimestamp
    @Column(name = "updated_on", nullable = false)
    private Instant updatedOn;

    public Address() {

    }

    public Address(String street, String postalCode) {
        this.street = street;
        this.postalCode = postalCode;
    }

    public String getFullAddress() {
        return this.city.getCountry().getName() + ", " + this.city.getName() + ", " + this.street + ", "
                + this.postalCode;
    }
}
