package nbu.team11.services;

import nbu.team11.dtos.EmployeeDto;
import nbu.team11.dtos.OfficeDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.entities.Employee;
import nbu.team11.entities.Office;
import nbu.team11.entities.Shipment;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.PositionType;
import nbu.team11.entities.enums.Role;
import nbu.team11.repositories.EmployeeRepository;
import nbu.team11.repositories.ShipmentRepository;
import nbu.team11.repositories.UserRepository;
import nbu.team11.services.exceptions.CannotDeleteEmployee;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.ResourceNotFound;
import nbu.team11.services.exceptions.UsernameNotAvailable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService Tests")
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserService userService;

    @Mock
    private OfficeService officeService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShipmentRepository shipmentRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private User testUser;
    private Office testOffice;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testUser = new User(Role.EMPLOYEE, "emp@example.com", "encodedPassword", "Maria", "Ivanova");
        testUser.setId(1);

        testOffice = new Office();
        testOffice.setId(1);
        testOffice.setTitle("Sofia Central");

        testEmployee = new Employee(testUser, testOffice, PositionType.COORDINATOR);
        testEmployee.setId(1);
    }

    @Test
    @DisplayName("create() should create employee successfully")
    void create_ShouldCreateEmployeeSuccessfully() throws UsernameNotAvailable, EmailNotAvailable {
        // Arrange
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setPosition(PositionType.COORDINATOR);
        employeeDto.setOfficeId(1);

        UserDto userDto = new UserDto();
        userDto.setEmail("new@example.com");
        userDto.setPassword("Pass123!");
        userDto.setFirstName("New");
        userDto.setLastName("Employee");

        when(userService.create(userDto)).thenReturn(testUser);
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        // Act
        employeeService.create(employeeDto, userDto);

        // Assert
        verify(userService).create(userDto);
        verify(employeeRepository).save(any(Employee.class));
        assertEquals(testUser.getId(), employeeDto.getUserId());
    }

    @Test
    @DisplayName("create() should propagate UsernameNotAvailable from UserService")
    void create_ShouldPropagateUsernameNotAvailable() throws UsernameNotAvailable, EmailNotAvailable {
        // Arrange
        EmployeeDto employeeDto = new EmployeeDto();
        UserDto userDto = new UserDto();
        userDto.setEmail("existing@example.com");

        when(userService.create(userDto)).thenThrow(new UsernameNotAvailable());

        // Act & Assert
        assertThrows(UsernameNotAvailable.class,
                () -> employeeService.create(employeeDto, userDto));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("getAllEmployees() should return employees excluding admins")
    void getAllEmployees_ShouldReturnEmployeesExcludingAdmins() {
        // Arrange
        Employee coordinator = new Employee(testUser, testOffice, PositionType.COORDINATOR);
        coordinator.setId(1);

        User user2 = new User(Role.EMPLOYEE, "del@example.com", "pass", "Georgi", "Dimitrov");
        user2.setId(2);
        Employee deliveryman = new Employee(user2, testOffice, PositionType.DELIVERYMAN);
        deliveryman.setId(2);

        List<PositionType> expectedPositions = Arrays.asList(PositionType.COORDINATOR, PositionType.DELIVERYMAN);

        when(employeeRepository.findAllByPositionTypeIn(expectedPositions))
                .thenReturn(List.of(coordinator, deliveryman));

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(employeeRepository).findAllByPositionTypeIn(argThat(positions ->
                !positions.contains(PositionType.ADMIN)));
    }

    @Test
    @DisplayName("getAllEmployees() should return null when repository returns null")
    void getAllEmployees_ShouldReturnNull_WhenRepositoryReturnsNull() {
        // Arrange
        when(employeeRepository.findAllByPositionTypeIn(anyList())).thenReturn(null);

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("update() should update employee successfully")
    void update_ShouldUpdateEmployeeSuccessfully() throws UsernameNotAvailable, EmailNotAvailable, ResourceNotFound {
        // Arrange
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(1);
        employeeDto.setPosition(PositionType.DELIVERYMAN);
        employeeDto.setOfficeId(1);

        UserDto userDto = new UserDto();
        userDto.setEmail("emp@example.com");

        OfficeDto officeDto = new OfficeDto();
        officeDto.setId(1);
        officeDto.setTitle("Sofia Central");

        when(userService.update(userDto)).thenReturn(true);
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        when(officeService.getById(1)).thenReturn(officeDto);
        when(employeeRepository.save(testEmployee)).thenReturn(testEmployee);

        // Act
        employeeService.update(employeeDto, userDto);

        // Assert
        assertEquals(PositionType.DELIVERYMAN, testEmployee.getPositionType());
        verify(employeeRepository).save(testEmployee);
    }

    @Test
    @DisplayName("update() should throw ResourceNotFound when employee does not exist")
    void update_ShouldThrowResourceNotFound_WhenEmployeeDoesNotExist() {
        // Arrange
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(999);

        UserDto userDto = new UserDto();
        userDto.setEmail("emp@example.com");

        when(userService.update(userDto)).thenReturn(true);
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFound.class,
                () -> employeeService.update(employeeDto, userDto));
    }

    @Test
    @DisplayName("getById() should return EmployeeDto when employee exists")
    void getById_ShouldReturnEmployeeDto_WhenEmployeeExists() throws ResourceNotFound {
        // Arrange
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));

        // Act
        EmployeeDto result = employeeService.getById(1);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("getById() should throw ResourceNotFound when employee does not exist")
    void getById_ShouldThrowResourceNotFound_WhenEmployeeDoesNotExist() {
        // Arrange
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFound.class,
                () -> employeeService.getById(999));
    }

    @Test
    @DisplayName("delete() should delete employee with no shipments")
    void delete_ShouldDeleteEmployee_WhenNoShipments() throws ResourceNotFound, CannotDeleteEmployee {
        // Arrange
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        when(shipmentRepository.findAllByEmployeeId(1)).thenReturn(List.of());

        // Act
        employeeService.delete(1, 99);

        // Assert
        verify(employeeRepository).delete(testEmployee);
        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("delete() should transfer shipments to colleague before deleting")
    void delete_ShouldTransferShipments_WhenShipmentsExist() throws ResourceNotFound, CannotDeleteEmployee {
        // Arrange
        Shipment shipment1 = new Shipment();
        shipment1.setId(1);
        shipment1.setEmployee(testEmployee);
        Shipment shipment2 = new Shipment();
        shipment2.setId(2);
        shipment2.setEmployee(testEmployee);

        User colleagueUser = new User(Role.EMPLOYEE, "colleague@example.com", "pass", "Nikolay", "Georgiev");
        colleagueUser.setId(3);
        Employee colleague = new Employee(colleagueUser, testOffice, PositionType.DELIVERYMAN);
        colleague.setId(2);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        when(shipmentRepository.findAllByEmployeeId(1)).thenReturn(List.of(shipment1, shipment2));
        when(employeeRepository.findByOfficeId(1)).thenReturn(List.of(testEmployee, colleague));

        // Act
        employeeService.delete(1, 99);

        // Assert
        assertEquals(colleague, shipment1.getEmployee());
        assertEquals(colleague, shipment2.getEmployee());
        verify(shipmentRepository, times(2)).save(any(Shipment.class));
        verify(employeeRepository).delete(testEmployee);
        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("delete() should throw CannotDeleteEmployee when deleting self")
    void delete_ShouldThrowCannotDeleteEmployee_WhenDeletingSelf() {
        // Arrange
        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));

        // Act & Assert
        CannotDeleteEmployee exception = assertThrows(CannotDeleteEmployee.class,
                () -> employeeService.delete(1, 1));
        assertEquals("You cannot delete your own account.", exception.getMessage());
    }

    @Test
    @DisplayName("delete() should throw CannotDeleteEmployee when no colleagues to transfer shipments")
    void delete_ShouldThrowCannotDeleteEmployee_WhenNoColleaguesAvailable() {
        // Arrange
        Shipment shipment = new Shipment();
        shipment.setId(1);
        shipment.setEmployee(testEmployee);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(testEmployee));
        when(shipmentRepository.findAllByEmployeeId(1)).thenReturn(List.of(shipment));
        when(employeeRepository.findByOfficeId(1)).thenReturn(List.of(testEmployee));

        // Act & Assert
        CannotDeleteEmployee exception = assertThrows(CannotDeleteEmployee.class,
                () -> employeeService.delete(1, 99));
        assertTrue(exception.getMessage().contains("no other employees"));
    }

    @Test
    @DisplayName("delete() should throw ResourceNotFound when employee does not exist")
    void delete_ShouldThrowResourceNotFound_WhenEmployeeDoesNotExist() {
        // Arrange
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFound.class,
                () -> employeeService.delete(999, 1));
    }
}
