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
import nbu.team11.entities.enums.Role;
import nbu.team11.repositories.EmployeeRepository;
import nbu.team11.services.contracts.IEmployeeService;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.ResourceNotFound;
import nbu.team11.services.exceptions.UsernameNotAvailable;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmployeeService implements IEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserService userService;
    private final OfficeService officeService;

    public Page<EmployeeDto> paginate(int page, int size) {
        Page<Employee> employees = employeeRepository.findAll(PageRequest.of(page, size));
        return employees.map(employee -> (new ModelMapperConfig()).modelMapper().map(employee, EmployeeDto.class));
    }

    @Override
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

    @Override
    public void update(EmployeeDto employeeDto, UserDto userDto)
            throws UsernameNotAvailable, EmailNotAvailable, ResourceNotFound {
        this.userService.update(userDto);
        Employee employee = this.employeeRepository.findById(employeeDto.getId()).orElseThrow(ResourceNotFound::new);

        OfficeDto officeDto = this.officeService.getById(employeeDto.getOfficeId());
        employee.setPositionType(employeeDto.getPosition());
        employee.setOffice((new ModelMapperConfig()).modelMapper().map(officeDto, Office.class));
        this.employeeRepository.save(employee);
    }

    @Override
    public EmployeeDto getById(Integer id) throws ResourceNotFound {
        return (new ModelMapperConfig()).modelMapper()
                .map(employeeRepository.findById(id).orElseThrow(ResourceNotFound::new), EmployeeDto.class);
    }
}
