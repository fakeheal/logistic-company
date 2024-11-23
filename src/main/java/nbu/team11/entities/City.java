package nbu.team11.entities;

import jakarta.persistence.*;

public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToOne
    @JoinColumn(name = "id")
    private Country country;
}
