package nbu.team11.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nbu.team11.entities.enums.Role;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
public class User {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @NotBlank(message = "First Name cannot be blank!")
    @Size(max = 20, message = "First name has to be up to 20 characters!")
    @Column(name = "FirstName", nullable = false)
    private String firstName;

    @Getter
    @Setter
    @NotBlank(message = "Last Name cannot be blank!")
    @Size(max = 20, message = "Last name has to be up to 20 characters!")
    @Column(name = "LastName", nullable = false)
    private String lastName;

    @Getter
    @Setter
    @NotBlank(message = "Username cannot be blank!")
    @Size(min = 5, max = 20, message = "Username has to be between 5 and 20 characters!")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Getter
    @Setter
    @NotBlank(message = "Password cannot be blank!")
    @Column(name = "password", nullable = false)
    private String password;

    @Getter
    @Setter
    @NotBlank(message = "Email cannot be blank!")
    @Email(message = "Invalid email address. Please enter a proper email!")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private Instant createdOn;
    @UpdateTimestamp
    @Column(name = "updated_on", nullable = false)
    private Instant updatedOn;

    public User() {
    }

    public User(Role role, String email, String password, String username, String firstName, String lastName) {
        this.role = role;
        this.email = email;
        this.password = password;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFullName() {
        return this.getFirstName() + " " + this.getLastName();
    }
}
