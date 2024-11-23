package nbu.team11.entities;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
