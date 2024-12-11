package nbu.team11.services.contracts;

import nbu.team11.dtos.OfficeDto;

import java.util.List;

public interface IOfficeService {
    List<OfficeDto> getAll();
}