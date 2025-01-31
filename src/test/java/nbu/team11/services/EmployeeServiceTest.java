package nbu.team11.services;

import nbu.team11.configurations.ModelMapperConfig;
import nbu.team11.dtos.EmployeeDto;
import nbu.team11.dtos.OfficeDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.entities.Employee;
import nbu.team11.entities.Office;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.PositionType;
import nbu.team11.repositories.EmployeeRepository;
import nbu.team11.services.EmployeeService;
import nbu.team11.services.OfficeService;
import nbu.team11.services.UserService;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.ResourceNotFound;
import nbu.team11.services.exceptions.UsernameNotAvailable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserService userService;

    @Mock
    private OfficeService officeService;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeDto employeeDto;
    private UserDto userDto;
    private User user;
    private OfficeDto officeDto;
    private Office office;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("testuser");

        userDto = new UserDto();
        userDto.setId(1);
        userDto.setUsername("testuser");

        office = new Office();
        office.setId(1);

        officeDto = new OfficeDto();
        officeDto.setId(1);

        employee = new Employee();
        employee.setId(1);
        employee.setUser(user);
        employee.setOffice(office);
        employee.setPositionType(PositionType.ADMIN);

        employeeDto = new EmployeeDto();
        employeeDto.setId(1);
        employeeDto.setUserId(user.getId());
        employeeDto.setOfficeId(office.getId());
        employeeDto.setPosition(PositionType.ADMIN);
    }

    @Test
    void testPaginate() {
        List<Employee> employees = List.of(employee);
        Page<Employee> employeePage = new PageImpl<>(employees);
        when(employeeRepository.findAll(any(PageRequest.class))).thenReturn(employeePage);

        Page<EmployeeDto> result = employeeService.paginate(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(employeeRepository).findAll(any(PageRequest.class));
    }

    @Test
    void testCreateEmployee() throws UsernameNotAvailable, EmailNotAvailable {
        when(userService.create(userDto)).thenReturn(user);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        employeeService.create(employeeDto, userDto);

        verify(userService).create(userDto);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployee() throws UsernameNotAvailable, EmailNotAvailable, ResourceNotFound {
        when(userService.update(userDto)).thenReturn(userDto);
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(officeService.getById(1)).thenReturn(officeDto);

        employeeService.update(employeeDto, userDto);

        verify(userService).update(userDto);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployeeNotFound() {
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> employeeService.update(employeeDto, userDto));
    }

    @Test
    void testGetById() throws ResourceNotFound {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));

        EmployeeDto result = employeeService.getById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(employeeRepository).findById(1);
    }

    @Test
    void testGetByIdNotFound() {
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> employeeService.getById(1));
    }
}
