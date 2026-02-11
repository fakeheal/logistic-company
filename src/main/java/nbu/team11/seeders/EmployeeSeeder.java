package nbu.team11.seeders;

import nbu.team11.entities.Employee;
import nbu.team11.entities.Office;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.PositionType;
import nbu.team11.entities.enums.Role;
import nbu.team11.repositories.EmployeeRepository;
import nbu.team11.repositories.OfficeRepository;
import nbu.team11.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(2)
public class EmployeeSeeder implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final OfficeRepository officeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeSeeder(EmployeeRepository employeeRepository,
                          UserRepository userRepository,
                          OfficeRepository officeRepository,
                          PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.officeRepository = officeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        if (employeeRepository.count() > 0) return;

        List<Office> offices = officeRepository.findAll();
        if (offices.isEmpty()) return;

        Office sofia = offices.get(0);
        Office plovdiv = offices.size() > 1 ? offices.get(1) : sofia;
        Office varna = offices.size() > 2 ? offices.get(2) : sofia;

        createEmployee("admin@example.com", "Admin123!", "Ivan", "Petrov", sofia, PositionType.ADMIN);
        createEmployee("coordinator@example.com", "Coordinator123!", "Maria", "Ivanova", sofia, PositionType.COORDINATOR);
        createEmployee("deliveryman@example.com", "Deliveryman123!", "Georgi", "Dimitrov", sofia, PositionType.DELIVERYMAN);
        createEmployee("coordinator2@example.com", "Coordinator123!", "Elena", "Todorova", plovdiv, PositionType.COORDINATOR);
        createEmployee("deliveryman2@example.com", "Deliveryman123!", "Nikolay", "Georgiev", varna, PositionType.DELIVERYMAN);
    }

    private void createEmployee(String email, String password, String firstName, String lastName,
                                Office office, PositionType position) {
        User user = new User(Role.EMPLOYEE, email, passwordEncoder.encode(password), firstName, lastName);
        userRepository.save(user);

        Employee employee = new Employee(user, office, position);
        employeeRepository.save(employee);
    }
}
