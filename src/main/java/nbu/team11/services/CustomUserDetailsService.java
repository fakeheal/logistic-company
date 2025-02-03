package nbu.team11.services;

import java.util.Arrays;

import nbu.team11.entities.Employee;
import nbu.team11.entities.enums.Role;
import nbu.team11.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import nbu.team11.entities.User;
import nbu.team11.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        if (user.getRole() == Role.EMPLOYEE) {
            Employee employee = employeeRepository.findByUserId(user.getId());

            return new CustomUserDetails(
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    Arrays.asList(new SimpleGrantedAuthority("EMPLOYEE")),
                    employee.getPositionTypeFormatted());
        }

        // TODO: Check how password is handled
        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                Arrays.asList(new SimpleGrantedAuthority("CLIENT")),
                null);
    }
}
