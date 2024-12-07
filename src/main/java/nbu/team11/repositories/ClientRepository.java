package nbu.team11.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import nbu.team11.entities.*;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client findByUserId(Integer userId);
}