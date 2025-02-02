import jakarta.servlet.http.HttpServletRequest;
import nbu.team11.controllers.UserController;
import nbu.team11.dtos.UserDto;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.Role;
import nbu.team11.services.CustomPermissionEvaluator;
import nbu.team11.services.UserService;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.UsernameNotAvailable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private CustomPermissionEvaluator customPermissionEvaluator;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testHomePage() {
        String view = userController.home(model);
        assertEquals("layouts/app", view);
    }

    @Test
    void testLogin_WhenUserAuthenticated_ShouldRedirect() {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String view = userController.login(model, new UserDto());
        assertEquals("layouts/app", view);
    }

    @Test
    void testLogin_WhenUserNotAuthenticated_ShouldReturnLoginPage() {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String view = userController.login(model, new UserDto());
        assertEquals("layouts/app", view);
    }

    @Test
    void testRegisterSave_WhenUserExists_ShouldReturnRegisterPage() {
        UserDto userDto = new UserDto();
        userDto.setUsername("existingUser");
        when(userService.getByUsername(anyString())).thenReturn(userDto);
        String view = userController.registerSave(userDto, model, request);
        verify(userService, times(1)).getByUsername(anyString());
        assertEquals("user/register", view);
    }

    @Test
    void testRegisterSave_WhenUserDoesNotExist_ShouldRedirectHome() throws UsernameNotAvailable, EmailNotAvailable {
        UserDto userDto = new UserDto();
        userDto.setUsername("newUser");
        when(userService.getByUsername(userDto.getUsername())).thenReturn(null);
        User user = new User();
        user.setUsername(userDto.getUsername());
        when(userService.create(userDto)).thenReturn(user);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(userDto.getUsername())).thenReturn(userDetails);
        Authentication authMock = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authMock);
        userController = new UserController(userService);
        ReflectionTestUtils.setField(userController, "userDetailsService", userDetailsService);
        ReflectionTestUtils.setField(userController, "authenticationManager", authenticationManager);
        String view = userController.registerSave(userDto, model, request);
        assertEquals("redirect:/home", view);
    }

}