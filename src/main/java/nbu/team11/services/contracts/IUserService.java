package nbu.team11.services.contracts;

import nbu.team11.dtos.UserDto;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.Role;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.ResourceNotFound;
import nbu.team11.services.exceptions.UsernameNotAvailable;

import java.util.List;

public interface IUserService {
    UserDto getByUsername(String username);
    User getUserById(Integer id);
    User create (UserDto userDto) throws UsernameNotAvailable, EmailNotAvailable;
    List<User> getUsersByRole(Role role);
    UserDto update(UserDto updatedUser) throws UsernameNotAvailable, EmailNotAvailable, ResourceNotFound;
    void delete(Integer id);
}