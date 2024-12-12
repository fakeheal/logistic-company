package nbu.team11.services;

import lombok.AllArgsConstructor;
import nbu.team11.configurations.ModelMapperConfig;
import nbu.team11.dtos.OfficeDto;
import nbu.team11.entities.Office;
import nbu.team11.repositories.OfficeRepository;
import nbu.team11.services.contracts.IOfficeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OfficeService implements IOfficeService {
    private final OfficeRepository officeRepository;

    @Override
    public List<OfficeDto> getAll() {
        return this.officeRepository
                .findAll()
                .stream()
                .map(office -> (new ModelMapperConfig()).modelMapper().map(office, OfficeDto.class))
                .toList();
    }
}
