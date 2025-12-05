package nbu.team11.configurations;

import nbu.team11.dtos.*;
import nbu.team11.entities.*;
import nbu.team11.entities.enums.Role;
import nbu.team11.repositories.ClientRepository;
import nbu.team11.repositories.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ModelMapperConfig {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setImplicitMappingEnabled(false);

        modelMapper.typeMap(Employee.class, EmployeeDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getUser().getId(),          EmployeeDto::setUserId);
                    mapping.map(src -> src.getUser().getFullName(),    EmployeeDto::setUserFullName);
                    mapping.map(src -> src.getUser().getEmail(),       EmployeeDto::setUserEmail);
                    mapping.map(src -> src.getOffice().getTitle(),     EmployeeDto::setOfficeTitle);
                });

        modelMapper.typeMap(Address.class, AddressDto.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getId(),                    AddressDto::setId);
                    mapping.map(src -> src.getCoordinates(),     AddressDto::setCoordinates);
                    mapping.map(src -> src.getFormattedAddress(),     AddressDto::setFormattedAddress);
                });

        modelMapper.typeMap(AddressDto.class, Address.class)
                .addMappings(mapping -> {
                    mapping.map(src -> src.getId(),                       Address::setId);
                    mapping.map(src -> src.getCoordinates(),     Address::setCoordinates);
                    mapping.map(src -> src.getFormattedAddress(),     Address::setFormattedAddress);
                });

        TypeMap<User, UserDto> userMap = modelMapper.emptyTypeMap(User.class, UserDto.class);
        userMap.addMappings(mapping -> {
            mapping.map(src -> src.getId().toString(),               UserDto::setId);
            mapping.map(User::getFirstName,                          UserDto::setFirstName);
            mapping.map(User::getLastName,                           UserDto::setLastName);
            mapping.map(User::getEmail,                              UserDto::setEmail);
            mapping.map(User::getPassword,                           UserDto::setPassword);
            mapping.map(User::getRole,                               UserDto::setRole);
        }).setPostConverter(context -> {
            User source      = context.getSource();
            UserDto dest     = context.getDestination();

            if (source.getRole() == Role.CLIENT) {
                var client = clientRepository.findByUserId(source.getId());
                if (client != null) {
                    ClientDto clientDto = modelMapper.map(client, ClientDto.class);
                    // map addresses list explicitly (in case strict mapping didn't)
                    if (client.getAddresses() != null && !client.getAddresses().isEmpty()) {
                        List<AddressDto> addressDtos = modelMapper.map(
                                client.getAddresses(),
                                new TypeToken<List<AddressDto>>() {}.getType()
                        );
                        clientDto.setAddresses(addressDtos);
                    }
                    dest.setClientDto(clientDto);
                }
            } else if (source.getRole() == Role.EMPLOYEE) {
                var employee = employeeRepository.findByUserId(source.getId());
                if (employee != null) {
                    EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);
                    dest.setEmployeeDto(employeeDto);
                }
            }
            return dest;
        });

        TypeMap<User, UserDtoSanitized> userMapSanitized = modelMapper.emptyTypeMap(User.class, UserDtoSanitized.class);
        userMapSanitized.addMappings(mapping -> {
            mapping.map(User::getFirstName,                          UserDtoSanitized::setFirstName);
            mapping.map(User::getLastName,                           UserDtoSanitized::setLastName);
            mapping.map(User::getEmail,                              UserDtoSanitized::setEmail);
            mapping.map(User::getRole,                               UserDtoSanitized::setRole);
        }).setPostConverter(context -> {
            User source      = context.getSource();
            UserDtoSanitized dest     = context.getDestination();

            if (source.getRole() == Role.CLIENT) {
                var client = clientRepository.findByUserId(source.getId());
                if (client != null) {
                    ClientDto clientDto = modelMapper.map(client, ClientDto.class);
                    if (client.getAddresses() != null && !client.getAddresses().isEmpty()) {
                        List<AddressDto> addressDtos = modelMapper.map(
                                client.getAddresses(),
                                new TypeToken<List<AddressDto>>() {}.getType()
                        );
                        clientDto.setAddresses(addressDtos);
                    }
                    dest.setClientDto(clientDto);
                }
            } else if (source.getRole() == Role.EMPLOYEE) {
                var employee = employeeRepository.findByUserId(source.getId());
                if (employee != null) {
                    EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);
                    dest.setEmployeeDto(employeeDto);
                }
            }
            return dest;
        });

        modelMapper.emptyTypeMap(UserDto.class, User.class)
                .addMappings(mapping -> {
                    mapping.map(UserDto::getFirstName, User::setFirstName);
                    mapping.map(UserDto::getLastName,  User::setLastName);
                    mapping.map(UserDto::getEmail,     User::setEmail);
                    mapping.map(UserDto::getPassword,  User::setPassword);
                    mapping.map(UserDto::getRole,      User::setRole);
                });

        modelMapper.typeMap(Shipment.class, ShipmentDto.class)
                .addMappings(mapping -> {
                    mapping.map(Shipment::getId,               ShipmentDto::setId);
                    mapping.map(Shipment::getUniqueId,         ShipmentDto::setUniqueID);
                    mapping.map(Shipment::isPayed,              ShipmentDto::setPayed);
                    mapping.map(Shipment::getWeight,           ShipmentDto::setWeight);
                    mapping.map(Shipment::getPrice,            ShipmentDto::setPrice);
                    mapping.map(Shipment::getCreatedOn,        ShipmentDto::setCreatedOn);
                    mapping.map(Shipment::getUpdatedOn,        ShipmentDto::setUpdatedOn);
                    mapping.map(Shipment::getEmployee,         ShipmentDto::setEmployee);
                    mapping.map(Shipment::getSenderClient,     ShipmentDto::setSenderClient);
                    mapping.map(Shipment::getRecipientClient,  ShipmentDto::setRecipientClient);
                    mapping.map(Shipment::getSendingOffice,    ShipmentDto::setSendingOffice);
                    mapping.map(Shipment::getReceivingOffice,    ShipmentDto::setReceivingOffice);
                    mapping.map(Shipment::getSenderAddress,    ShipmentDto::setSenderAddress);
                    mapping.map(Shipment::getRecipientAddress, ShipmentDto::setRecipientAddress);
                });

        modelMapper.typeMap(ShipmentDto.class, Shipment.class)
                .addMappings(mapping -> {
                    mapping.map(ShipmentDto::getUniqueID,        Shipment::setUniqueId);
                    mapping.map(ShipmentDto::isPayed,             Shipment::setPayed);
                    mapping.map(ShipmentDto::getWeight,          Shipment::setWeight);
                    mapping.map(ShipmentDto::getPrice,           Shipment::setPrice);
                    mapping.map(ShipmentDto::getEmployee,        Shipment::setEmployee);
                    mapping.map(ShipmentDto::getSenderClient,    Shipment::setSenderClient);
                    mapping.map(ShipmentDto::getRecipientClient, Shipment::setRecipientClient);
                    mapping.map(ShipmentDto::getSendingOffice,   Shipment::setSendingOffice);
                    mapping.map(ShipmentDto::getReceivingOffice, Shipment::setReceivingOffice);
                    mapping.map(ShipmentDto::getSenderAddress,   Shipment::setSenderAddress);
                    mapping.map(ShipmentDto::getRecipientAddress,Shipment::setRecipientAddress);
                });

        // Client -> ClientDto: map phone and addresses list
        modelMapper.typeMap(Client.class, ClientDto.class)
                .addMappings(mapping -> {
                    mapping.map(Client::getId,   ClientDto::setId);
                    mapping.map(Client::getPhoneNumber,   ClientDto::setPhoneNumber);
                    mapping.map(src -> src.getAddresses(),  ClientDto::setAddresses);
                });

        modelMapper.typeMap(Office.class, OfficeDto.class)
                .addMappings(mapping -> {
                    mapping.map(Office::getId,           OfficeDto::setId);
                    mapping.map(Office::getTitle,        OfficeDto::setTitle);
                    mapping.map(src -> src.getAddress(),  OfficeDto::setAddressDto);
                });

        return modelMapper;
    }
}
