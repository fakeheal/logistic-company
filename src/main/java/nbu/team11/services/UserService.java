package nbu.team11.services;
import nbu.team11.entities.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import nbu.team11.dtos.UserDto;
import nbu.team11.entities.*;
import nbu.team11.repositories.UserRepository;

@Service
public class UserService implements IUserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public User findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    @Override
    public User save(UserDto userDto) {
        User user = new User(Role.CLIENT, userDto.getEmail(), passwordEncoder.encode(userDto.getPassword()), userDto.getUsername());
        return userRepository.save(user);
    }
}