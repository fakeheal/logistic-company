package nbu.team11.repositories;

import nbu.team11.entities.Employee;
import nbu.team11.entities.enums.PositionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee findByUserId(Integer userId);
    Employee getReferenceById(Integer id);

    List<Employee> findByOfficeId(Integer officeId);

    List<Employee> findByPositionType(PositionType positionType);

}
