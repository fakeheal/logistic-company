package nbu.team11.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
