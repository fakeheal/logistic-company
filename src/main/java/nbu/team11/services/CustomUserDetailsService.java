package nbu.team11.services;
import java.util.Arrays;
import java.util.Collection;

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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if(user == null){
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        //TODO: Check how password is handled
        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                authorities());
    }

    public Collection<? extends GrantedAuthority> authorities () {
        return Arrays.asList(new SimpleGrantedAuthority("USER"));
    }
}
