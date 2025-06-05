//package nbu.team11.services.modelMapping;
//
//import nbu.team11.dtos.UserDto;
//import nbu.team11.entities.User;
//import nbu.team11.entities.enums.Role;
//
//public class CustomModelMapper {
//
//    private ShipmentRepository shipmentRepository;
//
//    public User toEntity(UserDto dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        User user = new User();
//        user.setEmail(dto.getEmail());
//        user.setPassword(dto.getPassword());
//        user.setRole(dto.getRole() != null ? dto.getRole() : Role.CLIENT); // default role
//        user.setFirstName(dto.getFirstName());
//        user.setLastName(dto.getLastName());
//        user.setId(Integer.parseInt(dto.getId()));
//
//        return user;
//    }
//
//    public UserDto toDto(User user) {
//        if (user == null) {
//            return null;
//        }
//
//        UserDto dto = new UserDto();
//        dto.setEmail(user.getEmail());
//        dto.setPassword(user.getPassword());
//        dto.setRole(user.getRole());
//        dto.setFirstName(user.getFirstName());
//        dto.setLastName(user.getLastName());
//
//        if (user.getId() != null) {
//            dto.setId(user.getId().toString());
//        }
//
//        return dto;
//    }
//
//    public ShipmentStatus toEntity(ShipmentStatusDto dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        ShipmentStatus entity = new ShipmentStatus();
//        entity.setId(dto.getId());
//        entity.setStatus(dto.getStatus());
//        entity.setComment(dto.getComment());
//
//        if (dto.getShipmentId() != null) {
//            Shipment shipment = shipmentRepository.findById(dto.getShipmentId())
//                    .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + dto.getShipmentId()));
//            entity.setShipment(shipment);
//        }
//
//        return entity;
//    }
//
//    public ShipmentStatusDto toDto(ShipmentStatus entity) {
//        if (entity == null) {
//            return null;
//        }
//
//        ShipmentStatusDto dto = new ShipmentStatusDto();
//        dto.setId(entity.getId());
//        dto.setStatus(entity.getStatus());
//        dto.setComment(entity.getComment());
//        dto.setCreatedOn(entity.getCreatedOn());
//        dto.setUpdatedOn(entity.getUpdatedOn());
//
//        if (entity.getShipment() != null) {
//            dto.setShipmentId(entity.getShipment().getId());
//        }
//
//        return dto;
//    }
//
//    public EmployeeDto toDto(Employee employee) {
//        if (employee == null) return null;
//        EmployeeDto dto = new EmployeeDto();
//        dto.setId(employee.getId());
//        dto.setPosition(employee.getPositionType());
//        dto.setCreatedOn(employee.getCreatedOn());
//        dto.setUpdatedOn(employee.getUpdatedOn());
//        if (employee.getUser() != null) {
//            dto.setUserFirstName(employee.getUser().getFirstName());
//            dto.setUserFullName(employee.getUser().getFullName());
//            dto.setUserEmail(employee.getUser().getEmail());
//            dto.setUserId(employee.getUser().getId());
//        }
//        if (employee.getOffice() != null) {
//            dto.setOfficeTitle(employee.getOffice().getTitle());
//            dto.setOffiserLastName(employee.getUser().getLastName());
//            dto.setUceId(employee.getOffice().getId());
//        }
//        return dto;
//    }
//
//    public Employee toEntity(EmployeeDto dto) {
//        if (dto == null) return null;
//        Employee employee = new Employee();
//        employee.setId(dto.getId());
//        employee.setPositionType(dto.getPosition());
//        // Assume setting user and office elsewhere (e.g., in service layer)
//        return employee;
//    }
//
//    public ClientDto toDto(Client client) {
//        if (client == null) return null;
//        ClientDto dto = new ClientDto();
//        dto.setPhoneNumber(client.getPhoneNumber());
//        dto.setAddressDto(toDto(client.getAddress()));
//        return dto;
//    }
//
//    public Client toEntity(ClientDto dto) {
//        if (dto == null) return null;
//        Client client = new Client();
//        client.setPhoneNumber(dto.getPhoneNumber());
//        client.setAddress(toEntity(dto.getAddressDto()));
//        return client;
//    }
//
//    public AddressDto toDto(Address address) {
//        if (address == null) return null;
//        AddressDto dto = new AddressDto();
//        dto.setStreet(address.getStreet());
//        dto.setPostalCode(address.getPostalCode());
//        if (address.getCity() != null) {
//            dto.setCityName(address.getCity().getName());
//        }
//        return dto;
//    }
//
//    public Address toEntity(AddressDto dto) {
//        if (dto == null) return null;
//        Address address = new Address();
//        address.setStreet(dto.getStreet());
//        address.setPostalCode(dto.getPostalCode());
//        // Assume setting City elsewhere (e.g., in service layer)
//        return address;
//    }
//
//    public OfficeDto toDto(Office office) {
//        if (office == null) return null;
//        OfficeDto dto = new OfficeDto();
//        dto.setId(office.getId());
//        dto.setTitle(office.getTitle());
//        dto.setCreatedOn(office.getCreatedOn());
//        dto.setUpdatedOn(office.getUpdatedOn());
//        return dto;
//    }
//
//    public Office toEntity(OfficeDto dto) {
//        if (dto == null) return null;
//        Office office = new Office();
//        office.setId(dto.getId());
//        office.setTitle(dto.getTitle());
//        // Assume setting address elsewhere
//        return office;
//    }
//
//    public ShipmentDto toDto(Shipment shipment) {
//        if (shipment == null) return null;
//        ShipmentDto dto = new ShipmentDto();
//        dto.setId(shipment.getId());
//        dto.setEmployee(toDto(shipment.getEmployee()));
//        dto.setRecipientClient(toDto(shipment.getRecipientClient()));
//        dto.setRecipientAddress(toDto(shipment.getRecipientAddress()));
//        dto.setSenderClient(toDto(shipment.getSenderClient()));
//        dto.setSenderAddress(toDto(shipment.getSenderAddress()));
//        dto.setOffice(toDto(shipment.getOffice()));
//        dto.setWeight(shipment.getWeight());
//        dto.setPrice(shipment.getPrice());
//        dto.setPayed(shipment.isPayed());
//        dto.setUniqueID(shipment.getUniqueId());
//        dto.setCreatedOn(shipment.getCreatedOn());
//        dto.setUpdatedOn(shipment.getUpdatedOn());
//        return dto;
//    }
//
//    public Shipment toEntity(ShipmentDto dto) {
//        if (dto == null) return null;
//        Shipment shipment = new Shipment();
//        shipment.setId(dto.getId());
//        shipment.setWeight(dto.getWeight());
//        shipment.setPrice(dto.getPrice());
//        shipment.setPayed(dto.isPayed());
//        shipment.setUniqueId(dto.getUniqueID());
//        shipment.setEmployee(toEntity(dto.getEmployee()));
//        shipment.setRecipientClient(toEntity(dto.getRecipientClient()));
//        shipment.setRecipientAddress(toEntity(dto.getRecipientAddress()));
//        shipment.setSenderClient(toEntity(dto.getSenderClient()));
//        shipment.setSenderAddress(toEntity(dto.getSenderAddress()));
//        shipment.setOffice(toEntity(dto.getOffice()));
//        return shipment;
//    }
//
//    public CountryDto toDto(Country country) {
//        if (country == null) return null;
//        CountryDto dto = new CountryDto();
//        dto.setId(country.getId());
//        dto.setName(country.getName());
//        dto.setCreatedOn(country.getCreatedOn());
//        dto.setUpdatedOn(country.getUpdatedOn());
//        return dto;
//    }
//
//    public Country toEntity(CountryDto dto) {
//        if (dto == null) return null;
//        Country country = new Country();
//        country.setId(dto.getId());
//        country.setName(dto.getName());
//        return country;
//    }
//}
