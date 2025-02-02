package nbu.team11.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nbu.team11.entities.enums.PositionType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
public class Employee {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @Setter
    @Enumerated(EnumType.STRING)
    private PositionType positionType;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private Instant createdOn;
    @UpdateTimestamp
    @Column(name = "updated_on", nullable = false)
    private Instant updatedOn;

    public Employee() {
    }

    public Employee(User user, Office office, PositionType positionType) {
        this.user = user;
        this.office = office;
        this.positionType = positionType;
    }

    public String getPositionTypeFormatted() {
        switch (this.getPositionType()) {
            case PositionType.ADMIN -> {
                return "Administrator";
            }
            case PositionType.COORDINATOR -> {
                return "Coordinator";
            }
            case PositionType.DELIVERYMAN -> {
                return "Employee";
            }
        }

        return "N/A";
    }

    private PositionType getPositionType() {
        return this.positionType;
    }
}
