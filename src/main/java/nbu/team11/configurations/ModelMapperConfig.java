package nbu.team11.configurations;

import nbu.team11.dtos.*;
import nbu.team11.entities.*;
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

        modelMapper.typeMap(Employee.class, EmployeeDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getUser().getFullName(), EmployeeDto::setUserFullName);
                    mapping.map(src -> src.getUser().getEmail(), EmployeeDto::setUserEmail);
                    mapping.map(src -> src.getOffice().getTitle(), EmployeeDto::setOfficeTitle);
                    mapping.map(src -> src.getUser().getId(), EmployeeDto::setUserId);
                });

        modelMapper.typeMap(Address.class, AddressDto.class)
                .addMappings(mapping -> {
                    mapping.map(Address::getStreet, AddressDto::setStreet);
                    mapping.map(Address::getPostalCode, AddressDto::setPostalCode);
                    mapping.map(src -> src.getCity().getName(), AddressDto::setCityName);
                });

        TypeMap<User, UserDto> userToDtoTypeMap = modelMapper.createTypeMap(User.class, UserDto.class);
        userToDtoTypeMap.addMappings(mapping -> {
            mapping.map(src -> src.getId().toString(), UserDto::setId);
            mapping.map(User::getFirstName, UserDto::setFirstName);
            mapping.map(User::getLastName, UserDto::setLastName);
            mapping.map(User::getEmail, UserDto::setEmail);
            mapping.map(User::getPassword, UserDto::setPassword);
            mapping.map(User::getRole, UserDto::setRole);
            mapping.map(User::getEmail, UserDto::setEmail); // Also used for username
        });

        userToDtoTypeMap.setPostConverter(context -> {
            User source = context.getSource();
            UserDto destination = context.getDestination();

            if (source.getAddress() != null) {
                AddressDto addressDto = modelMapper.map(source.getAddress(), AddressDto.class);
                destination.setAddressDto(addressDto);
            }

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

        modelMapper.createTypeMap(UserDto.class, User.class)
                .addMappings(mapping -> {
                    mapping.map(UserDto::getFirstName, User::setFirstName);
                    mapping.map(UserDto::getLastName, User::setLastName);
                    mapping.map(UserDto::getEmail, User::setEmail);
                    mapping.map(UserDto::getPassword, User::setPassword);
                    mapping.map(UserDto::getRole, User::setRole);
                });

        modelMapper.typeMap(Shipment.class, ShipmentDto.class)
                .addMappings(mapping -> {
                    mapping.map(Shipment::getId, ShipmentDto::setId);
                    mapping.map(Shipment::getEmployee, ShipmentDto::setEmployee);
                    mapping.map(Shipment::getRecipient, ShipmentDto::setRecipient);
                    mapping.map(Shipment::getRecipientAddress, ShipmentDto::setRecipientAddress);
                    mapping.map(Shipment::getSender, ShipmentDto::setSender);
                    mapping.map(Shipment::getSenderAddress, ShipmentDto::setSenderAddress);
                    mapping.map(Shipment::getOffice, ShipmentDto::setOffice);
                    mapping.map(Shipment::getWeight, ShipmentDto::setWeight);
                    mapping.map(Shipment::getPrice, ShipmentDto::setPrice);
                    mapping.map(Shipment::getUniqueId, ShipmentDto::setUniqueID);
                    mapping.map(Shipment::isPayed, ShipmentDto::setPayed);
                    mapping.map(Shipment::getCreatedOn, ShipmentDto::setCreatedOn);
                    mapping.map(Shipment::getUpdatedOn, ShipmentDto::setUpdatedOn);
                });

        modelMapper.typeMap(ShipmentDto.class, Shipment.class)
                .addMappings(mapping -> {
                    mapping.map(ShipmentDto::getEmployee, Shipment::setEmployee);
                    mapping.map(ShipmentDto::getRecipient, Shipment::setRecipient);
                    mapping.map(ShipmentDto::getRecipientAddress, Shipment::setRecipientAddress);
                    mapping.map(ShipmentDto::getSender, Shipment::setSender);
                    mapping.map(ShipmentDto::getSenderAddress, Shipment::setSenderAddress);
                    mapping.map(ShipmentDto::getOffice, Shipment::setOffice);
                    mapping.map(ShipmentDto::getWeight, Shipment::setWeight);
                    mapping.map(ShipmentDto::getPrice, Shipment::setPrice);
                    mapping.map(ShipmentDto::getUniqueID, Shipment::setUniqueId);
                    mapping.map(ShipmentDto::isPayed, Shipment::setPayed);
                });

        return modelMapper;
    }
}
