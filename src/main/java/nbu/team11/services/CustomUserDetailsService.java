package nbu.team11.services;

import nbu.team11.entities.Employee;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.Role;
import nbu.team11.repositories.EmployeeRepository;
import nbu.team11.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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
                    List.of(new SimpleGrantedAuthority(Role.EMPLOYEE.name())),
                    employee.getPositionType()
            );
        }

        //TODO: Check how password is handled
        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                List.of(new SimpleGrantedAuthority(Role.CLIENT.name())),
                null
        );
    }
}
