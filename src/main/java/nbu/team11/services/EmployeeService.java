package nbu.team11.services;

import lombok.AllArgsConstructor;
import nbu.team11.configurations.ModelMapperConfig;
import nbu.team11.dtos.EmployeeDto;
import nbu.team11.entities.Employee;
import nbu.team11.repositories.EmployeeRepository;
import nbu.team11.services.contracts.IEmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmployeeService implements IEmployeeService {
    private final EmployeeRepository employeeRepository;

    public Page<EmployeeDto> paginate(int page, int size) {
        Page<Employee> employees = employeeRepository.findAll(PageRequest.of(page, size));
        return employees.map(employee -> (new ModelMapperConfig()).modelMapper().map(employee, EmployeeDto.class));
    }
}
