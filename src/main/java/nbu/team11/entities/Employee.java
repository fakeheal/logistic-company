package nbu.team11.entities;

import jakarta.persistence.*;
import lombok.Data;
import nbu.team11.entities.enums.PositionType;
import nbu.team11.entities.enums.Role;


@Entity
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "office_id")
    private Office office;

    @Enumerated(EnumType.STRING)
    private PositionType positionType;

    private String firstName;
    private String lastName;
}
