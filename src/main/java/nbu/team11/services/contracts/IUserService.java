package nbu.team11.services.contracts;

import nbu.team11.dtos.UserDto;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.Role;

import java.util.List;

public interface IUserService {
    UserDto getByUsername(String username);
    User getUserById(Integer id);
    User create (UserDto userDto);
    List<User> getUsersByRole(Role role);
    boolean update(UserDto updatedUser);
    void delete(Integer id);
}