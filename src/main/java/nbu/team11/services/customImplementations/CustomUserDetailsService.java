package nbu.team11.services.customImplementations;
import java.util.Arrays;
import java.util.Collection;

import nbu.team11.services.customImplementations.models.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import nbu.team11.entities.User;
import nbu.team11.repositories.UserRepository;

@Service
public class CustomUserDetailsService  implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        return new CustomUserDetails(
                user.getPassword(),
                user.getEmail(),
                authorities());
    }

    public Collection<? extends GrantedAuthority> authorities () {
        return Arrays.asList(new SimpleGrantedAuthority("USER"));
    }
}
