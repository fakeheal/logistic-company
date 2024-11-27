package nbu.team11.services;

import nbu.team11.dtos.UserDto;
import nbu.team11.entities.User;

public interface IUserService {
    User findByUsername(String username);
    User save (UserDto userDto);
}