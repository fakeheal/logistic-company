package nbu.team11.repositories;

import nbu.team11.entities.User;
import nbu.team11.entities.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findByRole(Role role);
}
