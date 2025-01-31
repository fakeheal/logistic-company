package nbu.team11.services;

import nbu.team11.entities.Employee;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.PositionType;
import nbu.team11.entities.enums.Role;
import nbu.team11.repositories.EmployeeRepository;
import nbu.team11.repositories.UserRepository;
import nbu.team11.services.CustomUserDetails;
import nbu.team11.services.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;
    private Employee employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Създаване на тестов User
        user = new User();
        user.setId(1);
        user.setUsername("testUser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setRole(Role.EMPLOYEE);

        // Създаване на тестов Employee с позиция
        employee = new Employee();
        employee.setUser(user);
        employee.setPositionType(PositionType.COORDINATOR);
    }

    @Test
    void testLoadUserByUsername_WhenUserExistsAndIsEmployee() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(employeeRepository.findByUserId(user.getId())).thenReturn(employee);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testUser");
        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertEquals("test@example.com", ((CustomUserDetails) userDetails).getEmail());
        assertEquals("Coordinator", ((CustomUserDetails) userDetails).getPositionType());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("EMPLOYEE")));
    }

    @Test
    void testLoadUserByUsername_WhenUserExistsAndIsClient() {
        user.setRole(Role.CLIENT);
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testUser");
        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertEquals("test@example.com", ((CustomUserDetails) userDetails).getEmail());
        assertNull(((CustomUserDetails) userDetails).getPositionType()); // Клиентите нямат позиция
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("CLIENT")));
    }

    @Test
    void testLoadUserByUsername_WhenUserNotFound() {
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(null);

        Exception exception = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("nonExistentUser"));

        assertEquals("User not found with email: nonExistentUser", exception.getMessage());
    }
}
