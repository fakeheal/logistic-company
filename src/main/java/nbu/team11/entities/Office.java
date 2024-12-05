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

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
