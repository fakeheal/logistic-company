package nbu.team11.services;

import nbu.team11.dtos.AddressDto;
import nbu.team11.entities.Address;
import nbu.team11.repositories.AddressRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AddressService(AddressRepository addressRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    public Address create(AddressDto dto) {
        Address entity = modelMapper.map(dto, Address.class);
        Address saved = addressRepository.save(entity);
        return saved;
    }

    public Optional<AddressDto> findById(Integer id) {
        return addressRepository.findById(id)
                .map(address -> modelMapper.map(address, AddressDto.class));
    }
}
