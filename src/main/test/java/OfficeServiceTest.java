import nbu.team11.configurations.ModelMapperConfig;
import nbu.team11.dtos.OfficeDto;
import nbu.team11.entities.Office;
import nbu.team11.repositories.OfficeRepository;
import nbu.team11.services.OfficeService;
import nbu.team11.services.exceptions.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfficeServiceTest {

    @Mock
    private OfficeRepository officeRepository;

    @InjectMocks
    private OfficeService officeService;

    private Office office;
    private OfficeDto officeDto;

    @BeforeEach
    void setUp() {
        office = new Office();
        office.setId(1);
        office.setTitle("Main Office");

        officeDto = new OfficeDto();
        officeDto.setId(1);
        officeDto.setTitle("Main Office");
    }

    @Test
    void testGetAll() {
        when(officeRepository.findAll()).thenReturn(List.of(office));
        List<OfficeDto> result = officeService.getAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Main Office", result.get(0).getTitle());
        verify(officeRepository).findAll();
    }

    @Test
    void testGetById() throws ResourceNotFound {
        when(officeRepository.findById(1)).thenReturn(Optional.of(office));
        OfficeDto result = officeService.getById(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Main Office", result.getTitle());
        verify(officeRepository).findById(1);
    }

    @Test
    void testGetByIdNotFound() {
        when(officeRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class, () -> officeService.getById(1));
    }

    @Test
    void testGetAllEmptyList() {
        when(officeRepository.findAll()).thenReturn(List.of());
        List<OfficeDto> result = officeService.getAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(officeRepository).findAll();
    }
}