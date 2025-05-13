package nbu.team11.configurations;

import nbu.team11.dtos.ClientDto;
import nbu.team11.dtos.EmployeeDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.entities.Employee;
import nbu.team11.entities.User;
import nbu.team11.entities.enums.Role;
import nbu.team11.repositories.ClientRepository;
import nbu.team11.repositories.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private EmployeeRepository employeeRepository;


    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Employee → EmployeeDto mapping
        modelMapper
                .typeMap(Employee.class, EmployeeDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getUser().getFullName(), EmployeeDto::setUserFullName);
                    mapping.map(src -> src.getUser().getEmail(), EmployeeDto::setUserEmail);
                    mapping.map(src -> src.getOffice().getTitle(), EmployeeDto::setOfficeTitle);
                    mapping.map(src -> src.getUser().getId(), EmployeeDto::setUserId);
                });

        // User → UserDto mapping with Client/EmployeeDto fetch
        TypeMap<User, UserDto> userToDtoTypeMap = modelMapper.createTypeMap(User.class, UserDto.class);
        userToDtoTypeMap.addMappings(mapping -> {
            mapping.map(src -> src.getId().toString(), UserDto::setId);
            mapping.map(User::getFirstName, UserDto::setFirstName);
            mapping.map(User::getLastName, UserDto::setLastName);
            mapping.map(User::getEmail, UserDto::setEmail);
            mapping.map(User::getPassword, UserDto::setPassword);
            mapping.map(User::getRole, UserDto::setRole);
            mapping.map(User::getEmail, UserDto::setEmail);
        });

        userToDtoTypeMap.setPostConverter(context -> {
            User source = context.getSource();
            UserDto destination = context.getDestination();

            if (source.getRole() == Role.CLIENT) {
                var client = clientRepository.findByUserId(source.getId());
                if (client != null) {
                    ClientDto clientDto = modelMapper.map(client, ClientDto.class);
                    destination.setClientDto(clientDto);
                }
            } else if (source.getRole() == Role.EMPLOYEE) {
                var employee = employeeRepository.findByUserId(source.getId());
                if (employee != null) {
                    EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);
                    destination.setEmployeeDto(employeeDto);
                }
            }

            return destination;
        });

        // UserDto → User mapping
        TypeMap<UserDto, User> dtoToUserTypeMap = modelMapper.createTypeMap(UserDto.class, User.class);
        dtoToUserTypeMap.addMappings(mapping -> {
            mapping.map(UserDto::getFirstName, User::setFirstName);
            mapping.map(UserDto::getLastName, User::setLastName);
            mapping.map(UserDto::getEmail, User::setEmail);
            mapping.map(UserDto::getPassword, User::setPassword);
            mapping.map(UserDto::getRole, User::setRole);
        });

        return modelMapper;
    }
}
