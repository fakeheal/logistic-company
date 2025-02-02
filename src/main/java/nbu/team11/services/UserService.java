package nbu.team11.services;

import nbu.team11.dtos.UserDto;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.Role;
import nbu.team11.repositories.UserRepository;
import nbu.team11.services.contracts.IUserService;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.ResourceNotFound;
import nbu.team11.services.exceptions.UsernameNotAvailable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

//TODO: Revise all methods
//TODO: Test all methods
//TODO: Exception handling
@Service
public class UserService implements IUserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    private UserRepository userRepository;
    private ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository, ModelMapper modelMapper) {

        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    // TODO: Return UserDTO with automapper
    @Override
    public UserDto getByUsername(String username) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return null;
        }

        UserDto userDto = this.modelMapper.map(user, UserDto.class);

        return userDto;
    }

    // TODO: Return UserDTO with automapper
    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    // TODO: Return UserDTO with automapper
    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public UserDto update(UserDto userDto) throws UsernameNotAvailable, EmailNotAvailable, ResourceNotFound {
        User user = this.userRepository.findByUsername(userDto.getUsername());
        if (user != null && !Objects.equals(user.getId(), userDto.getId())) {
            throw new UsernameNotAvailable();
        }

        user = this.userRepository.findByEmail(userDto.getEmail());
        if (user != null && !Objects.equals(user.getId(), userDto.getId())) {
            throw new EmailNotAvailable();
        }

        User userToUpdate = userRepository.findById(userDto.getId()).orElseThrow(ResourceNotFound::new);

        userToUpdate.setRole(userDto.getRole());
        userToUpdate.setEmail(userDto.getEmail());
        userToUpdate.setUsername(userDto.getUsername());
        userToUpdate.setFirstName(userDto.getFirstName());
        userToUpdate.setLastName(userDto.getLastName());

        if (userDto.getPassword() != null) {
            userToUpdate.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        return this.modelMapper.map(userRepository.save(userToUpdate), UserDto.class);
    }

    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    // TODO: Return automap user dto
    @Override
    public User create(UserDto userDto) throws UsernameNotAvailable, EmailNotAvailable {
        if (this.userRepository.findByUsername(userDto.getUsername()) != null) {
            throw new UsernameNotAvailable();
        }

        if (this.userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new EmailNotAvailable();
        }

        User user = new User(userDto.getRole(), userDto.getEmail(), passwordEncoder.encode(userDto.getPassword()),
                userDto.getUsername(), userDto.getFirstName(), userDto.getLastName());
        return userRepository.save(user);
    }
}