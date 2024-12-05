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

    public void setUser(User user) {
        this.user = user;
    }

    public void setOffice(Office office) {
        this.office = office;
    }

    public void setPositionType(PositionType positionType) {
        this.positionType = positionType;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Office getOffice() {
        return office;
    }

    public PositionType getPositionType() {
        return positionType;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
