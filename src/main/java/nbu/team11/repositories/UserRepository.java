package nbu.team11.repositories;

import nbu.team11.entities.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import nbu.team11.entities.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    List<User> findByRole(Role role);
}
