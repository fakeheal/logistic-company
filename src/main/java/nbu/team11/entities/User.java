package nbu.team11.entities;

import jakarta.persistence.*;
import lombok.Data;
import nbu.team11.entities.enums.Role;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
