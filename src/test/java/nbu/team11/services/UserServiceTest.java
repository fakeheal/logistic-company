package nbu.team11.services;

import nbu.team11.dtos.UserDto;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.Role;
import nbu.team11.repositories.UserRepository;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.ResourceNotFound;
import nbu.team11.services.exceptions.UsernameNotAvailable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setRole(Role.CLIENT);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("encodedPassword");

        userDto = new UserDto();
        userDto.setId(1);
        userDto.setUsername("testUser");
        userDto.setEmail("test@example.com");
        userDto.setRole(Role.CLIENT);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setPassword("plainPassword");
    }

    @Test
    void testGetByUsername_WhenUserExists_ShouldReturnUserDto() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.getByUsername("testUser");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void testGetByUsername_WhenUserDoesNotExist_ShouldReturnNull() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(null);

        UserDto result = userService.getByUsername("unknownUser");

        assertNull(result);
    }

    @Test
    void testGetUserById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void testGetUserById_WhenUserDoesNotExist_ShouldReturnNull() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        User result = userService.getUserById(1);

        assertNull(result);
    }

    @Test
    void testGetUsersByRole_ShouldReturnUsersList() {
        when(userRepository.findByRole(Role.CLIENT)).thenReturn(List.of(user));

        List<User> result = userService.getUsersByRole(Role.CLIENT);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testUpdate_WhenUserExists_ShouldUpdateSuccessfully() throws Exception {
        when(userRepository.findByUsername("testUser")).thenReturn(null); // Username available
        when(userRepository.findByEmail("test@example.com")).thenReturn(null); // Email available
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.update(userDto);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdate_WhenUsernameTaken_ShouldThrowException() {
        User anotherUser = new User();
        anotherUser.setId(2);
        anotherUser.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(anotherUser);

        assertThrows(UsernameNotAvailable.class, () -> userService.update(userDto));
    }

    @Test
    void testUpdate_WhenEmailTaken_ShouldThrowException() {
        User anotherUser = new User();
        anotherUser.setId(2);
        anotherUser.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(anotherUser);

        assertThrows(EmailNotAvailable.class, () -> userService.update(userDto));
    }

    @Test
    void testUpdate_WhenUserDoesNotExist_ShouldThrowResourceNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> userService.update(userDto));
    }

    @Test
    void testDelete_ShouldDeleteUser() {
        userService.delete(1);

        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void testCreate_WhenUsernameAndEmailAvailable_ShouldCreateUser() throws Exception {
        when(userRepository.findByUsername("testUser")).thenReturn(null); // Username available
        when(userRepository.findByEmail("test@example.com")).thenReturn(null); // Email available
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.create(userDto);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreate_WhenUsernameTaken_ShouldThrowException() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        assertThrows(UsernameNotAvailable.class, () -> userService.create(userDto));
    }

    @Test
    void testCreate_WhenEmailTaken_ShouldThrowException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        assertThrows(EmailNotAvailable.class, () -> userService.create(userDto));
    }
}