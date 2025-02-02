import com.fasterxml.jackson.databind.ObjectMapper;
import nbu.team11.controllers.ShipmentController;
import nbu.team11.dtos.ShipmentDto;
import nbu.team11.entities.Shipment;
import nbu.team11.entities.ShipmentStatus;
import nbu.team11.entities.enums.Status;
import nbu.team11.services.ShipmentService;
import nbu.team11.services.exceptions.ShipmentNotFound;
import nbu.team11.services.exceptions.UnauthorizedAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ShipmentControllerTest {

    @Mock
    private ShipmentService shipmentService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ShipmentController shipmentController;

    private MockMvc mockMvc;
    private Shipment shipment;
    private ShipmentDto shipmentDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(shipmentController).build();

        shipment = new Shipment();
        shipment.setId(1);
        shipment.setWeight(5.0);

        shipmentDto = new ShipmentDto();
        shipmentDto.setId(1);
        shipmentDto.setWeight(5.0);
    }

    @Test
    void testGetAllShipments_ShouldReturnShipments() throws Exception {
        when(shipmentService.getAllShipmentsForEmployee(any())).thenReturn(List.of(shipment));
        when(modelMapper.map(any(Shipment.class), eq(ShipmentDto.class))).thenReturn(shipmentDto);

        mockMvc.perform(get("/shipments/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(shipmentService, times(1)).getAllShipmentsForEmployee(any());
    }

    @Test
    void testGetShipmentsByEmployee_ShouldReturnShipments() throws Exception {
        when(shipmentService.getShipmentsByEmployeeId(1)).thenReturn(List.of(shipment));
        when(modelMapper.map(any(Shipment.class), eq(ShipmentDto.class))).thenReturn(shipmentDto);

        mockMvc.perform(get("/shipments/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(shipmentService, times(1)).getShipmentsByEmployeeId(1);
    }

    @Test
    void testGetUndeliveredShipments_ShouldReturnUndeliveredShipments() throws Exception {
        when(shipmentService.getUndeliveredShipments()).thenReturn(List.of(shipment));
        when(modelMapper.map(any(Shipment.class), eq(ShipmentDto.class))).thenReturn(shipmentDto);

        mockMvc.perform(get("/shipments/undelivered"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));

        verify(shipmentService, times(1)).getUndeliveredShipments();
    }

    @Test
    void testRegisterShipment_WhenAuthorized_ShouldRegisterShipment() throws Exception {
        when(modelMapper.map(any(ShipmentDto.class), eq(Shipment.class))).thenReturn(shipment);
        when(shipmentService.registerShipment(any(Shipment.class), any())).thenReturn(shipment);
        when(modelMapper.map(any(Shipment.class), eq(ShipmentDto.class))).thenReturn(shipmentDto);

        mockMvc.perform(post("/shipments/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"weight\":5.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(shipmentService, times(1)).registerShipment(any(Shipment.class), any());
    }

    @Test
    void testRegisterShipment_WhenUnauthorized_ShouldReturnForbidden() throws Exception {
        ShipmentDto shipmentDto = new ShipmentDto();
        shipmentDto.setId(1);
        shipmentDto.setWeight(5.0);

        Shipment mappedShipment = new Shipment();
        mappedShipment.setId(1);
        mappedShipment.setWeight(5.0);

        when(modelMapper.map(any(ShipmentDto.class), eq(Shipment.class))).thenReturn(mappedShipment);
        when(shipmentService.registerShipment(eq(mappedShipment), any()))
                .thenThrow(new UnauthorizedAccess("Unauthorized access!"));

        mockMvc.perform(post("/shipments/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(shipmentDto)))
                .andExpect(status().isForbidden()); // Очаквано поведение: 403 Forbidden

        verify(shipmentService, times(1)).registerShipment(eq(mappedShipment), any());
    }
    @Test
    void testUpdateShipmentStatus_ShouldUpdateStatus() throws Exception {
        when(shipmentService.updateShipmentStatus(eq(1), eq(Status.IN_TRANSIT))).thenReturn(shipment);
        when(modelMapper.map(any(Shipment.class), eq(ShipmentDto.class))).thenReturn(shipmentDto);

        mockMvc.perform(patch("/shipments/status/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"IN_TRANSIT\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(shipmentService, times(1)).updateShipmentStatus(1, Status.IN_TRANSIT);
    }

    @Test
    void testUpdateShipmentStatus_WhenShipmentNotFound_ShouldReturnNotFound() throws Exception {
        when(shipmentService.updateShipmentStatus(eq(1), any(Status.class))).thenThrow(new ShipmentNotFound("The shipment is not found!"));

        mockMvc.perform(patch("/shipments/status/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"IN_TRANSIT\""))
                .andExpect(status().isNotFound());

        verify(shipmentService, times(1)).updateShipmentStatus(1, Status.IN_TRANSIT);
    }

    @Test
    void testGetShipmentHistory_ShouldReturnHistory() throws Exception {
        ShipmentStatus status = new ShipmentStatus();
        status.setStatus(Status.DELIVERED);

        when(shipmentService.getShipmentHistory(1)).thenReturn(List.of(status));

        mockMvc.perform(get("/shipments/history/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].status", is("DELIVERED")));

        verify(shipmentService, times(1)).getShipmentHistory(1);
    }

    @Test
    void testGetShipmentStatistics_ShouldReturnStatistics() throws Exception {
        when(shipmentService.getShipmentStatistics()).thenReturn(Map.of("totalShipments", 5));

        mockMvc.perform(get("/shipments/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalShipments", is(5)));

        verify(shipmentService, times(1)).getShipmentStatistics();
    }

    @Test
    void testDeleteShipment_WhenExists_ShouldDelete() throws Exception {
        mockMvc.perform(delete("/shipments/1"))
                .andExpect(status().isNoContent());

        verify(shipmentService, times(1)).deleteShipment(1);
    }

    @Test
    void testDeleteShipment_WhenNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new ShipmentNotFound("The shipment is not found!")).when(shipmentService).deleteShipment(1);

        mockMvc.perform(delete("/shipments/1"))
                .andExpect(status().isNotFound());

        verify(shipmentService, times(1)).deleteShipment(1);
    }
}