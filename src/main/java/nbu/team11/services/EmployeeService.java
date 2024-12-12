package nbu.team11.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nbu.team11.configurations.ModelMapperConfig;
import nbu.team11.dtos.EmployeeDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.entities.Employee;
import nbu.team11.entities.User;
import nbu.team11.repositories.EmployeeRepository;
import nbu.team11.services.contracts.IEmployeeService;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.UsernameNotAvailable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmployeeService implements IEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserService userService;

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
                        .map(employeeDto, Employee.class)
        );
    }

    @Override
    public EmployeeDto getById(Integer id) {
        return (new ModelMapperConfig()).modelMapper().map(employeeRepository.getReferenceById(id), EmployeeDto.class);
    }
}
