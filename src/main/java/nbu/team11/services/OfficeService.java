package nbu.team11.services;

import lombok.AllArgsConstructor;
import nbu.team11.configurations.ModelMapperConfig;
import nbu.team11.dtos.OfficeDto;
import nbu.team11.repositories.OfficeRepository;
import nbu.team11.services.contracts.IOfficeService;
import nbu.team11.services.exceptions.ResourceNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OfficeService implements IOfficeService {
    @Autowired
    private final OfficeRepository officeRepository;

    @Override
    public List<OfficeDto> getAll() {
        var offices = this.officeRepository
                .findAll()
                .stream()
                .map(office -> (new ModelMapperConfig()).modelMapper().map(office, OfficeDto.class))
                .toList();

        return this.officeRepository
                .findAll()
                .stream()
                .map(office -> (new ModelMapperConfig()).modelMapper().map(office, OfficeDto.class))
                .toList();
    }

    @Override
    public OfficeDto getById(Integer id) throws ResourceNotFound {
        return (new ModelMapperConfig()).modelMapper().map(officeRepository.findById(id).orElseThrow(ResourceNotFound::new), OfficeDto.class);
    }
}
