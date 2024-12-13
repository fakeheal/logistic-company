package nbu.team11.services.contracts;

import nbu.team11.dtos.OfficeDto;
import nbu.team11.services.exceptions.ResourceNotFound;

import java.util.List;

public interface IOfficeService {
    List<OfficeDto> getAll();

    OfficeDto getById(Integer id) throws ResourceNotFound;
}