package nbu.team11.services.contracts;

import nbu.team11.dtos.EmployeeDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.ResourceNotFound;
import nbu.team11.services.exceptions.UsernameNotAvailable;
import org.springframework.data.domain.Page;

public interface IEmployeeService {
    Page<EmployeeDto> paginate(int page, int size);

    void create(EmployeeDto employeeDto, UserDto userDto) throws UsernameNotAvailable, EmailNotAvailable;

    void update(EmployeeDto employeeDto, UserDto userDto) throws UsernameNotAvailable, EmailNotAvailable, ResourceNotFound;

    EmployeeDto getById(Integer id) throws ResourceNotFound;
}