package nbu.team11.services;

import lombok.AllArgsConstructor;
import nbu.team11.dtos.OfficeDto;
import nbu.team11.entities.Address;
import nbu.team11.entities.Office;
import nbu.team11.repositories.AddressRepository;
import nbu.team11.repositories.OfficeRepository;
import nbu.team11.services.exceptions.ResourceNotFound;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class OfficeService {
    @Autowired
    private final OfficeRepository officeRepository;

    @Autowired
    private final AddressRepository addressRepository;

    @Autowired
    private final ModelMapper modelMapper;

    public List<OfficeDto> getAll() {
        return this.officeRepository
                .findAll()
                .stream()
                .map(office -> modelMapper.map(office, OfficeDto.class))
                .toList();
    }

    public OfficeDto getById(Integer id) throws ResourceNotFound {
        return modelMapper.map(officeRepository.findById(id).orElseThrow(ResourceNotFound::new), OfficeDto.class);
    }

    public Page<OfficeDto> paginate(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);

        if (search != null && !search.isBlank()) {
            return officeRepository.findByAddressContaining(search, pageable)
                    .map(office -> modelMapper.map(office, OfficeDto.class));
        }

        return officeRepository.findAll(pageable)
                .map(office -> modelMapper.map(office, OfficeDto.class));
    }

    @Transactional
    public OfficeDto create(String title, String formattedAddress, String coordinates) {
        Address address = new Address();
        address.setFormattedAddress(formattedAddress);
        address.setCoordinates(coordinates);
        addressRepository.save(address);

        Office office = new Office();
        office.setTitle(title);
        office.setAddress(address);
        Office saved = officeRepository.save(office);

        return modelMapper.map(saved, OfficeDto.class);
    }

    @Transactional
    public OfficeDto update(Integer id, String title, String formattedAddress, String coordinates) throws ResourceNotFound {
        Office office = officeRepository.findById(id).orElseThrow(ResourceNotFound::new);

        office.setTitle(title);

        Address address = office.getAddress();
        address.setFormattedAddress(formattedAddress);
        address.setCoordinates(coordinates);
        addressRepository.save(address);

        Office saved = officeRepository.save(office);
        return modelMapper.map(saved, OfficeDto.class);
    }

    @Transactional
    public void delete(Integer id) throws ResourceNotFound {
        Office office = officeRepository.findById(id).orElseThrow(ResourceNotFound::new);
        Address address = office.getAddress();

        officeRepository.delete(office);

        if (address != null) {
            addressRepository.delete(address);
        }
    }
}
