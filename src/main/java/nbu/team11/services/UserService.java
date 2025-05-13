package nbu.team11.services;
import nbu.team11.configurations.ModelMapperConfig;
import nbu.team11.entities.enums.Role;
import nbu.team11.services.contracts.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import nbu.team11.dtos.UserDto;
import nbu.team11.entities.*;
import nbu.team11.repositories.UserRepository;

import java.util.List;

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

    @Override
    public UserDto getByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if(user == null)
        {
            return null;
        }

        return this.modelMapper.map(user, UserDto.class);
    }

    //TODO: Return UserDTO with automapper
    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    //TODO: Return UserDTO with automapper
    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public boolean update(UserDto updatedUser) {
        User user = userRepository.findByEmail(updatedUser.getEmail());
        if (user == null) return false;

        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        userRepository.save(user);

        return true;
    }


    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    //TODO: Return automap user dto
    @Override
    public User create(UserDto userDto) {
        User user = new User(Role.CLIENT, userDto.getEmail(), passwordEncoder.encode(userDto.getPassword()), userDto.getFirstName(), userDto.getLastName());
        return userRepository.save(user);
    }
}