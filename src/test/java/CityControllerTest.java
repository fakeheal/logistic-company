import nbu.team11.controllers.CityController;
import nbu.team11.dtos.CityDto;
import nbu.team11.services.CityService;
import nbu.team11.services.exceptions.CityNotFound;
import nbu.team11.services.exceptions.CountryNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CityControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CityService cityService;

    @InjectMocks
    private CityController cityController;

    private CityDto cityDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cityController).build();

        cityDto = new CityDto();
        cityDto.setId(1);
        cityDto.setName("Sofia");
    }

    @Test
    void testGetAllCities_ShouldReturnListOfCities() throws Exception {
        when(cityService.getAllCities()).thenReturn(List.of(cityDto));

        mockMvc.perform(get("/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Sofia"));

        verify(cityService, times(1)).getAllCities();
    }

    @Test
    void testGetCityById_WhenExists_ShouldReturnCity() throws Exception {
        when(cityService.getCityById(1)).thenReturn(cityDto);

        mockMvc.perform(get("/cities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sofia"));

        verify(cityService, times(1)).getCityById(1);
    }

    @Test
    void testGetCityById_WhenNotExists_ShouldReturn404() throws Exception {
        when(cityService.getCityById(99)).thenThrow(new CityNotFound("City not found"));

        mockMvc.perform(get("/cities/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("City not found"));

        verify(cityService, times(1)).getCityById(99);
    }

    @Test
    void testCreateCity_ShouldCreateCity() throws Exception {
        when(cityService.createCity(any(CityDto.class))).thenReturn(cityDto);

        mockMvc.perform(post("/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Sofia\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sofia"));

        verify(cityService, times(1)).createCity(any(CityDto.class));
    }

    @Test
    void testUpdateCity_WhenExists_ShouldUpdateAndReturnCity() throws Exception {
        when(cityService.updateCity(eq(1), any(CityDto.class))).thenReturn(cityDto);

        mockMvc.perform(put("/cities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Sofia\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sofia"));

        verify(cityService, times(1)).updateCity(eq(1), any(CityDto.class));
    }

    @Test
    void testUpdateCity_WhenNotExists_ShouldReturn404() throws Exception {
        when(cityService.updateCity(eq(99), any(CityDto.class))).thenThrow(new CityNotFound("City not found"));

        mockMvc.perform(put("/cities/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Unknown\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("City not found"));

        verify(cityService, times(1)).updateCity(eq(99), any(CityDto.class));
    }

    @Test
    void testDeleteCity_WhenExists_ShouldReturnNoContent() throws Exception {
        doNothing().when(cityService).deleteCity(1);

        mockMvc.perform(delete("/cities/1"))
                .andExpect(status().isNoContent());

        verify(cityService, times(1)).deleteCity(1);
    }

    @Test
    void testDeleteCity_WhenNotExists_ShouldReturn404() throws Exception {
        doThrow(new CityNotFound("City not found")).when(cityService).deleteCity(99);

        mockMvc.perform(delete("/cities/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("City not found"));

        verify(cityService, times(1)).deleteCity(99);
    }

    @Test
    void testHandleCountryNotFound_ShouldReturn404WithMessage() throws Exception {
        when(cityService.createCity(any(CityDto.class))).thenThrow(new CountryNotFound("Country not found"));

        mockMvc.perform(post("/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Unknown\", \"countryId\": 1}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Country not found"));

        verify(cityService, times(1)).createCity(any(CityDto.class));
    }
}
