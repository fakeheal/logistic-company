import nbu.team11.controllers.CountryController;
import nbu.team11.dtos.CountryDto;
import nbu.team11.services.CountryService;
import nbu.team11.services.exceptions.CountryNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CountryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryController countryController;

    private CountryDto countryDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(countryController).build();

        countryDto = new CountryDto();
        countryDto.setId(1);
        countryDto.setName("Bulgaria");
    }

    @Test
    void testGetAllCountries_ShouldReturnListOfCountries() throws Exception {
        when(countryService.getAllCountries()).thenReturn(List.of(countryDto));

        mockMvc.perform(get("/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Bulgaria"));

        verify(countryService, times(1)).getAllCountries();
    }

    @Test
    void testGetCountryById_WhenExists_ShouldReturnCountry() throws Exception {
        when(countryService.getCountryById(1)).thenReturn(countryDto);

        mockMvc.perform(get("/countries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bulgaria"));

        verify(countryService, times(1)).getCountryById(1);
    }

    @Test
    void testGetCountryById_WhenNotExists_ShouldReturn404() throws Exception {
        when(countryService.getCountryById(99)).thenThrow(new CountryNotFound("Country not found"));

        mockMvc.perform(get("/countries/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Country not found"));

        verify(countryService, times(1)).getCountryById(99);
    }

    @Test
    void testCreateCountry_ShouldCreateCountry() throws Exception {
        when(countryService.createCountry(any(CountryDto.class))).thenReturn(countryDto);

        mockMvc.perform(post("/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Bulgaria\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bulgaria"));

        verify(countryService, times(1)).createCountry(any(CountryDto.class));
    }

   @Test
    void testUpdateCountry_WhenExists_ShouldUpdateAndReturnCountry() throws Exception {
        when(countryService.updateCountry(eq(1), any(CountryDto.class))).thenReturn(countryDto);

        mockMvc.perform(put("/countries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Bulgaria\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bulgaria"));

        verify(countryService, times(1)).updateCountry(eq(1), any(CountryDto.class));
    }

    @Test
    void testUpdateCountry_WhenNotExists_ShouldReturn404() throws Exception {
        when(countryService.updateCountry(eq(99), any(CountryDto.class))).thenThrow(new CountryNotFound("Country not found"));

        mockMvc.perform(put("/countries/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Unknown\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Country not found"));

        verify(countryService, times(1)).updateCountry(eq(99), any(CountryDto.class));
    }

    @Test
    void testDeleteCountry_WhenExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(countryService).deleteCountry(1);

        mockMvc.perform(delete("/countries/1"))
                .andExpect(status().isNoContent());

        verify(countryService, times(1)).deleteCountry(1);
    }

    @Test
    void testDeleteCountry_WhenNotExists_ShouldReturn404() throws Exception {
        doThrow(new CountryNotFound("Country not found")).when(countryService).deleteCountry(99);

        mockMvc.perform(delete("/countries/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Country not found"));

        verify(countryService, times(1)).deleteCountry(99);
    }
}
