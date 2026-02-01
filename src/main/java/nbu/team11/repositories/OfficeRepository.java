package nbu.team11.repositories;

import nbu.team11.entities.Office;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Integer> {
    Office findByTitle(String title);

    @Query("SELECT o FROM Office o WHERE LOWER(o.address.formattedAddress) LIKE LOWER(CONCAT('%', :address, '%'))")
    Page<Office> findByAddressContaining(@Param("address") String address, Pageable pageable);

    Page<Office> findAll(Pageable pageable);
}