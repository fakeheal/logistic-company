package nbu.team11.controllers;

import nbu.team11.entities.Employee;
import nbu.team11.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService productService;

    @RequestMapping()
    public String index(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Page<Employee> employees = productService.getEmployees(page, size);

        model.addAttribute("title", "All Employees");
        model.addAttribute("content", "employee/index");
        model.addAttribute("employees", employees);

        return "layouts/app";
    }
}
