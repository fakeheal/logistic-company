package nbu.team11.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nbu.team11.configurations.ModelMapperConfig;
import nbu.team11.dtos.EmployeeDto;
import nbu.team11.dtos.OfficeDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.entities.Employee;
import nbu.team11.entities.Office;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.PositionType;
import nbu.team11.repositories.EmployeeRepository;
import nbu.team11.repositories.ShipmentRepository;
import nbu.team11.services.exceptions.CannotDeleteEmployee;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.ResourceNotFound;
import nbu.team11.services.exceptions.UsernameNotAvailable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserService userService;
    private final OfficeService officeService;
    private final nbu.team11.repositories.UserRepository userRepository;
    private final ShipmentRepository shipmentRepository;

    public Page<EmployeeDto> paginate(int page, int size) {
        Page<Employee> employees = employeeRepository.findAll(PageRequest.of(page, size));
        return employees.map(employee -> (new ModelMapperConfig()).modelMapper().map(employee, EmployeeDto.class));
    }

    @Transactional
    public void create(EmployeeDto employeeDto, UserDto userDto) throws UsernameNotAvailable, EmailNotAvailable {
        User user = this.userService.create(userDto);
        employeeDto.setUserId(user.getId());

        this.employeeRepository.save(
                (new ModelMapperConfig())
                        .modelMapper()
                        .map(employeeDto, Employee.class));
    }

    public List<Employee> getAllEmployees() {
        List<PositionType> positions = Arrays.stream(PositionType.values())
                .filter(position -> position != PositionType.ADMIN)
                .collect(Collectors.toList());

        List<Employee> employees = employeeRepository.findAllByPositionTypeIn(positions);

        if (employees == null) {
            return null;
        }

        return employees;
    }

    public void update(EmployeeDto employeeDto, UserDto userDto)
            throws UsernameNotAvailable, EmailNotAvailable, ResourceNotFound {
        this.userService.update(userDto);
        Employee employee = this.employeeRepository.findById(employeeDto.getId()).orElseThrow(ResourceNotFound::new);

        OfficeDto officeDto = this.officeService.getById(employeeDto.getOfficeId());
        employee.setPositionType(employeeDto.getPosition());
        employee.setOffice((new ModelMapperConfig()).modelMapper().map(officeDto, Office.class));
        this.employeeRepository.save(employee);
    }

    public EmployeeDto getById(Integer id) throws ResourceNotFound {
        return (new ModelMapperConfig()).modelMapper()
                .map(employeeRepository.findById(id).orElseThrow(ResourceNotFound::new), EmployeeDto.class);
    }

    @Transactional
    public void delete(Integer id, Integer currentUserId) throws ResourceNotFound, CannotDeleteEmployee {
        Employee employee = employeeRepository.findById(id).orElseThrow(ResourceNotFound::new);
        User user = employee.getUser();

        // Prevent admin from deleting themselves
        if (user != null && user.getId().equals(currentUserId)) {
            throw new CannotDeleteEmployee("You cannot delete your own account.");
        }

        // Find shipments assigned to this employee
        var shipments = shipmentRepository.findAllByEmployeeId(id);

        if (!shipments.isEmpty()) {
            // Find another employee in the same office
            List<Employee> colleaguesInOffice = employeeRepository.findByOfficeId(employee.getOffice().getId())
                    .stream()
                    .filter(e -> !e.getId().equals(id))
                    .toList();

            if (colleaguesInOffice.isEmpty()) {
                throw new CannotDeleteEmployee("Cannot delete this employee. They have " + shipments.size() +
                        " shipment(s) assigned and there are no other employees in the same office to transfer them to.");
            }

            // Transfer shipments to the first available colleague
            Employee newAssignee = colleaguesInOffice.get(0);
            for (var shipment : shipments) {
                shipment.setEmployee(newAssignee);
                shipmentRepository.save(shipment);
            }
        }

        employeeRepository.delete(employee);

        if (user != null) {
            userRepository.delete(user);
        }
    }
}
