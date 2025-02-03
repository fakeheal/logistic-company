package nbu.team11.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    private CustomUserDetails customUserDetails;
    private Collection<? extends GrantedAuthority> authorities;

    @BeforeEach
    void setUp() {
        authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        customUserDetails = new CustomUserDetails("testUser", "password123", "test@example.com", authorities, "ADMIN");
    }

    @Test
    void testGetUsername() {
        assertEquals("testUser", customUserDetails.getUsername());
    }

    @Test
    void testGetPassword() {
        assertEquals("password123", customUserDetails.getPassword());
    }

    @Test
    void testGetEmail() {
        assertEquals("test@example.com", customUserDetails.getEmail());
    }

    @Test
    void testGetAuthorities() {
        assertNotNull(customUserDetails.getAuthorities());
        assertEquals(1, customUserDetails.getAuthorities().size());
        assertTrue(customUserDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testGetPositionType() {
        assertEquals("ADMIN", customUserDetails.getPositionType());
    }

    @Test
    void testSetPositionType() {
        customUserDetails.setPositionType("COORDINATOR");
        assertEquals("COORDINATOR", customUserDetails.getPositionType());
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(customUserDetails.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(customUserDetails.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(customUserDetails.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(customUserDetails.isEnabled());
    }
}
