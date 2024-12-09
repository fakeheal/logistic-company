package nbu.team11.configurations;

import nbu.team11.dtos.EmployeeDto;
import nbu.team11.entities.Employee;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper
                .typeMap(Employee.class, EmployeeDto.class)
                .addMappings(
                        mapping -> {
                            mapping.map(src -> src.getUser().getFullName(), EmployeeDto::setUserFullName);
                            mapping.map(src -> src.getUser().getEmail(), EmployeeDto::setUserEmail);
                            mapping.map(src -> src.getOffice().getTitle(), EmployeeDto::setOfficeTitle);
                        }
                );

        return modelMapper;
    }
}