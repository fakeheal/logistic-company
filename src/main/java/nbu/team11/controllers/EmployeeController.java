package nbu.team11.controllers;

import jakarta.validation.Valid;
import nbu.team11.controllers.forms.CreateEmployeeForm;
import nbu.team11.controllers.forms.ui.SelectUtil;
import nbu.team11.dtos.EmployeeDto;
import nbu.team11.dtos.OfficeDto;
import nbu.team11.entities.enums.PositionType;
import nbu.team11.services.EmployeeService;
import nbu.team11.services.OfficeService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final OfficeService officeService;

    public EmployeeController(EmployeeService employeeService, OfficeService officeService) {
        this.employeeService = employeeService;
        this.officeService = officeService;
    }

    @RequestMapping()
    public String index(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<EmployeeDto> employees = employeeService.paginate(page, size);

        model.addAttribute("title", "All Employees ");
        model.addAttribute("content", "employee/index");
        model.addAttribute("employees", employees);

        return "layouts/app";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("title", "Create Employee");
        model.addAttribute("content", "employee/create");
        model.addAttribute("offices", SelectUtil.fromList(officeService.getAll(), officeDto -> String.valueOf(officeDto.getId()), OfficeDto::getTitle));
        model.addAttribute("positions", SelectUtil.fromEnum(PositionType.class));

        // If we were redirected from the store method, we should have the form in the model
        // including its errors
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new CreateEmployeeForm());
        }


        model.getAttribute("form");

        return "layouts/app";
    }

    @PostMapping("/store")
    public String store(@Valid CreateEmployeeForm createEmployeeForm, BindingResult bindingResult, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            attributes.addFlashAttribute("form", createEmployeeForm);
            return "redirect:/employee/create";
        }

        attributes.addFlashAttribute("message", "Employee created successfully");
        return "redirect:/employee";
    }

}
