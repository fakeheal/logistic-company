package nbu.team11.services;

import nbu.team11.dtos.UserDto;
import nbu.team11.dtos.UserDtoSanitized;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.Role;
import nbu.team11.repositories.UserRepository;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.UsernameNotAvailable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;
    private UserDtoSanitized testUserDtoSanitized;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);

        testUser = new User(Role.CLIENT, "client@example.com", "encodedPassword", "Ivan", "Petrov");
        testUser.setId(1);

        testUserDto = new UserDto();
        testUserDto.setEmail("client@example.com");
        testUserDto.setRole(Role.CLIENT);
        testUserDto.setFirstName("Ivan");
        testUserDto.setLastName("Petrov");

        testUserDtoSanitized = new UserDtoSanitized();
        testUserDtoSanitized.setEmail("client@example.com");
        testUserDtoSanitized.setRole(Role.CLIENT);
        testUserDtoSanitized.setFirstName("Ivan");
        testUserDtoSanitized.setLastName("Petrov");
    }

    @Test
    @DisplayName("getAllUsers() should return list of all users as sanitized DTOs")
    void getAllUsers_ShouldReturnListOfSanitizedDtos() {
        // Arrange
        User user2 = new User(Role.EMPLOYEE, "emp@example.com", "pass", "Maria", "Ivanova");
        user2.setId(2);

        UserDtoSanitized dto2 = new UserDtoSanitized();
        dto2.setEmail("emp@example.com");
        dto2.setRole(Role.EMPLOYEE);

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));
        when(modelMapper.map(testUser, UserDtoSanitized.class)).thenReturn(testUserDtoSanitized);
        when(modelMapper.map(user2, UserDtoSanitized.class)).thenReturn(dto2);

        // Act
        List<UserDtoSanitized> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("client@example.com", result.get(0).getEmail());
        assertEquals("emp@example.com", result.get(1).getEmail());
    }

    @Test
    @DisplayName("getAllUsers() should return empty list when no users exist")
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());

        // Act
        List<UserDtoSanitized> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getByEmail() should return UserDto when user exists")
    void getByEmail_ShouldReturnUserDto_WhenUserExists() {
        // Arrange
        when(userRepository.findByEmail("client@example.com")).thenReturn(testUser);
        when(modelMapper.map(testUser, UserDto.class)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.getByEmail("client@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("client@example.com", result.getEmail());
        assertEquals("Ivan", result.getFirstName());
    }

    @Test
    @DisplayName("getByEmail() should return null when user does not exist")
    void getByEmail_ShouldReturnNull_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        // Act
        UserDto result = userService.getByEmail("nonexistent@example.com");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("getUserById() should return User when user exists")
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("client@example.com", result.getEmail());
    }

    @Test
    @DisplayName("getUserById() should return null when user does not exist")
    void getUserById_ShouldReturnNull_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        User result = userService.getUserById(999);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("getUsersByRole() should return users with specified role")
    void getUsersByRole_ShouldReturnUsersWithRole() {
        // Arrange
        List<User> clients = List.of(testUser);
        when(userRepository.findByRole(Role.CLIENT)).thenReturn(clients);

        // Act
        List<User> result = userService.getUsersByRole(Role.CLIENT);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Role.CLIENT, result.get(0).getRole());
    }

    @Test
    @DisplayName("update() should return true when user is updated successfully")
    void update_ShouldReturnTrue_WhenUserUpdatedSuccessfully() {
        // Arrange
        UserDto updateDto = new UserDto();
        updateDto.setEmail("client@example.com");
        updateDto.setRole(Role.EMPLOYEE);

        when(userRepository.findByEmail("client@example.com")).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        boolean result = userService.update(updateDto);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("update() should return false when user does not exist")
    void update_ShouldReturnFalse_WhenUserDoesNotExist() {
        // Arrange
        UserDto updateDto = new UserDto();
        updateDto.setEmail("nonexistent@example.com");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        // Act
        boolean result = userService.update(updateDto);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("create() should create user successfully")
    void create_ShouldCreateUserSuccessfully() throws UsernameNotAvailable, EmailNotAvailable {
        // Arrange
        UserDto newUserDto = new UserDto();
        newUserDto.setEmail("new@example.com");
        newUserDto.setPassword("Password123!");
        newUserDto.setFirstName("Georgi");
        newUserDto.setLastName("Dimitrov");

        when(userRepository.findByEmail("new@example.com")).thenReturn(null);
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(2);
            return saved;
        });

        // Act
        User result = userService.create(newUserDto);

        // Assert
        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        assertEquals("Georgi", result.getFirstName());
        assertEquals("Dimitrov", result.getLastName());
        assertEquals(Role.CLIENT, result.getRole());
        verify(passwordEncoder).encode("Password123!");
    }

    @Test
    @DisplayName("create() should throw UsernameNotAvailable when email already exists")
    void create_ShouldThrowUsernameNotAvailable_WhenEmailExists() {
        // Arrange
        UserDto newUserDto = new UserDto();
        newUserDto.setEmail("client@example.com");
        newUserDto.setPassword("Password123!");

        when(userRepository.findByEmail("client@example.com")).thenReturn(testUser);

        // Act & Assert
        assertThrows(UsernameNotAvailable.class,
                () -> userService.create(newUserDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete() should call repository deleteById")
    void delete_ShouldCallRepositoryDeleteById() {
        // Arrange
        doNothing().when(userRepository).deleteById(1);

        // Act
        userService.delete(1);

        // Assert
        verify(userRepository, times(1)).deleteById(1);
    }
}
