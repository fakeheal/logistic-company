package nbu.team11.services.contracts;

import nbu.team11.dtos.EmployeeDto;
import org.springframework.data.domain.Page;

public interface IEmployeeService {
    Page<EmployeeDto> paginate(int page, int size);

    EmployeeDto create(EmployeeDto employeeDto);
}