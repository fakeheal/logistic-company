package nbu.team11.services;

import lombok.AllArgsConstructor;
import nbu.team11.dtos.OfficeDto;
import nbu.team11.repositories.OfficeRepository;
import nbu.team11.services.contracts.IOfficeService;
import nbu.team11.services.exceptions.ResourceNotFound;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OfficeService implements IOfficeService {
    @Autowired
    private final OfficeRepository officeRepository;

    @Autowired
    private final ModelMapper modelMapper;

    @Override
    public List<OfficeDto> getAll() {
        return this.officeRepository
                .findAll()
                .stream()
                .map(office -> modelMapper.map(office, OfficeDto.class))
                .toList();
    }

    @Override
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
}
