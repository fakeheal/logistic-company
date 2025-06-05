package nbu.team11.controllers;

import nbu.team11.dtos.OfficeDto;
import nbu.team11.services.OfficeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offices")
public class OfficeController {

    private final OfficeService officeService;

    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    @GetMapping
    public List<OfficeDto> getAllOffices() {
        return officeService.getAll();
    }
}
