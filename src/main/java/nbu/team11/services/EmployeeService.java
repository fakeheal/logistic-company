package nbu.team11.services;

import nbu.team11.entities.Employee;
import nbu.team11.entities.enums.PositionType;
import nbu.team11.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO: Revise all methods
//TODO: Test all methods
//TODO: Exception handling
@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee getEmployeeByUserId(Integer userId) {
        return employeeRepository.findByUserId(userId);
    }

    public Employee getEmployeeById(Integer id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public List<Employee> getEmployeesByOffice(Integer officeId) {
        return employeeRepository.findByOfficeId(officeId);
    }

    public List<Employee> getEmployeesByPosition(PositionType positionType) {
        return employeeRepository.findByPositionType(positionType);
    }

    public Employee updateEmployee(Integer id, Employee updatedEmployee) {
        Employee employee = getEmployeeById(id);
        if (employee == null) return null;
        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        employee.setPositionType(updatedEmployee.getPositionType());
        employee.setOffice(updatedEmployee.getOffice());
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Integer id) {
        employeeRepository.deleteById(id);
    }
}
