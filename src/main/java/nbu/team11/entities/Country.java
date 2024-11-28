package nbu.team11.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String name;
}
