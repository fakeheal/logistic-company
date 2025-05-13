package nbu.team11.services.customImplementations.models;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import nbu.team11.dtos.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
    private String password;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    @Getter
    private UserDto userDto;

    public CustomUserDetails(String password, String email, Collection<? extends GrantedAuthority> authorities, UserDto userDto) {
        this.password = password;
        this.email = email;
        this.authorities = authorities;
        this.userDto = userDto;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return authorities;
    }

    @Override
    public String getPassword() {

        return password;
    }

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return email;
    }

    public String getEmail() {
        // TODO Auto-generated method stub
        return email;
    }

    public String getRole() {
        // TODO Auto-generated method stub
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;
    }
}