package nbu.team11.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CustomPermissionEvaluatorTest {

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails userDetails;

    private CustomPermissionEvaluator customPermissionEvaluator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customPermissionEvaluator = new CustomPermissionEvaluator();
    }

    @Test
    void testHasRoleAndPosition_ValidRoleAndPosition() {
        when(userDetails.getPositionType()).thenReturn("ADMIN");

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        boolean result = customPermissionEvaluator.hasRoleAndPosition(authentication, "ROLE_ADMIN", "ADMIN");

        assertTrue(result, "Should return true when role and position match");
    }

    @Test
    void testHasRoleAndPosition_WrongRole() {
        when(userDetails.getPositionType()).thenReturn("ADMIN");

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // Грешна роля

        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        boolean result = customPermissionEvaluator.hasRoleAndPosition(authentication, "ROLE_ADMIN", "ADMIN");

        assertFalse(result, "Should return false when role does not match");
    }

    @Test
    void testHasRoleAndPosition_WrongPosition() {
        when(userDetails.getPositionType()).thenReturn("USER"); // Грешна позиция

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        boolean result = customPermissionEvaluator.hasRoleAndPosition(authentication, "ROLE_ADMIN", "ADMIN");

        assertFalse(result, "Should return false when position does not match");
    }

    @Test
    void testHasRoleAndPosition_AnonymousUser() {
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        boolean result = customPermissionEvaluator.hasRoleAndPosition(authentication, "ROLE_ADMIN", "ADMIN");

        assertFalse(result, "Should return false when authentication is anonymousUser");
    }

    @Test
    void testHasRoleAndPosition_NullAuthentication() {
        boolean result = customPermissionEvaluator.hasRoleAndPosition(null, "ROLE_ADMIN", "ADMIN");

        assertFalse(result, "Should return false when authentication is null");
    }
}
