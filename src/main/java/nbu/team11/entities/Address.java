package nbu.team11.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "CityId", nullable = false)
    private City city;

    @NotBlank(message = "Street cannot be blank!")
    @Size(min = 5, max = 20, message = "Street name has to be between 5 and 20 characters!")
    @Column(name = "Street", nullable = false)
    private String street;

    @NotBlank(message = "Postal code cannot be blank!")
    @Column(name = "PostalCode", nullable = false)
    private String postalCode;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private Instant createdOn;
    @UpdateTimestamp
    @Column(name = "updated_on", nullable = false)
    private Instant updatedOn;

    public Address(String street, String postalCode) {
        this.street = street;
        this.postalCode = postalCode;
    }

    public Integer getId() {
        return id;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

}
