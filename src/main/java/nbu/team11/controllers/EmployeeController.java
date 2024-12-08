package nbu.team11.controllers;

import nbu.team11.dtos.EmployeeDto;
import nbu.team11.services.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService productService;

    public EmployeeController(EmployeeService productService) {
        this.productService = productService;
    }

    @RequestMapping()
    public String index(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<EmployeeDto> employees = productService.paginate(page, size);

        model.addAttribute("title", "All Employees");
        model.addAttribute("content", "employee/index");
        model.addAttribute("employees", employees);

        return "layouts/app";
    }
}
