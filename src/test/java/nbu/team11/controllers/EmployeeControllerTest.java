package nbu.team11.controllers;

import nbu.team11.dtos.EmployeeDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.services.EmployeeService;
import nbu.team11.services.OfficeService;
import nbu.team11.services.exceptions.ResourceNotFound;
import nbu.team11.services.exceptions.UsernameNotAvailable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private OfficeService officeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
    }

    @Test
    void testGetEmployeeById_Success() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(1);
        employeeDto.setUserFullName("John Doe");
        when(employeeService.getById(1)).thenReturn(employeeDto);

        mockMvc.perform(get("/employee/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("employee"))
                .andExpect(view().name("layouts/app"));
    }

    @Test
    void testGetEmployeeById_NotFound() throws Exception {
        when(employeeService.getById(1)).thenThrow(new ResourceNotFound());

        mockMvc.perform(get("/employee/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateEmployee_Success() throws Exception {
        doNothing().when(employeeService).create(any(EmployeeDto.class), any(UserDto.class));

        mockMvc.perform(post("/employee/store")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("username", "johndoe")
                .param("email", "johndoe@example.com")
                .param("password", "password123")
                .param("confirmPassword", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee/create"));
    }

    @Test
    void testCreateEmployee_UsernameExists() throws Exception {
        doThrow(new UsernameNotAvailable()).when(employeeService).create(any(EmployeeDto.class), any(UserDto.class));

        mockMvc.perform(post("/employee/store")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "existinguser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee/create"));
    }

    @Test
    void testUpdateEmployee_Success() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(1);
        when(employeeService.getById(anyInt())).thenReturn(employeeDto);
        doNothing().when(employeeService).update(any(EmployeeDto.class), any(UserDto.class));

        mockMvc.perform(post("/employee/1/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", "Updated Name"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee/1/edit"));
    }

    @Test
    void testUpdateEmployee_NotFound() throws Exception {
        when(employeeService.getById(anyInt())).thenThrow(new ResourceNotFound());

        mockMvc.perform(post("/employee/1/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstName", "Updated Name"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/employee/1/edit*"));
    }

}