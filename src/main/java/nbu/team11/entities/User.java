package nbu.team11.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import nbu.team11.entities.enums.Role;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "First Name cannot be blank!")
    @Size(max = 20, message = "First name has to be up to 20 characters!")
    @Column(name = "FirstName", nullable = false)
    private String firstName;

    @NotBlank(message = "Last Name cannot be blank!")
    @Size(max = 20, message = "Last name has to be up to 20 characters!")
    @Column(name = "LastName", nullable = false)
    private String lastName;

    @NotBlank(message = "Username cannot be blank!")
    @Size(min = 5, max = 20, message = "Username has to be between 5 and 20 characters!")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password cannot be blank!")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "Email cannot be blank!")
    @Email(message = "Invalid email address. Please enter a proper email!")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public String getFullName() {
        return this.getFirstName() + " " + this.getLastName();
    }
}
