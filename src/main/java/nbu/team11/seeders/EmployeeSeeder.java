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

        Office office = officeRepository.findAll().stream().findFirst().orElse(null);
        if (office == null) return;

        User adminUser = new User(Role.EMPLOYEE,
                "admin@example.com",
                passwordEncoder.encode("Admin123!"),
                "Admin",
                "Administrator");
        userRepository.save(adminUser);

        Employee admin = new Employee(adminUser, office, PositionType.ADMIN);
        employeeRepository.save(admin);

        User coordinatorUser = new User(Role.EMPLOYEE,
                "coordinator@example.com",
                passwordEncoder.encode("Coordinator123!"),
                "Coordinator",
                "Smith");
        userRepository.save(coordinatorUser);

        Employee coordinator = new Employee(coordinatorUser, office, PositionType.COORDINATOR);
        employeeRepository.save(coordinator);

        User deliverymanUser = new User(Role.EMPLOYEE,
                "deliveryman@example.com",
                passwordEncoder.encode("Deliveryman123!"),
                "Delivery",
                "Person");
        userRepository.save(deliverymanUser);

        Employee deliveryman = new Employee(deliverymanUser, office, PositionType.DELIVERYMAN);
        employeeRepository.save(deliveryman);
    }
}
