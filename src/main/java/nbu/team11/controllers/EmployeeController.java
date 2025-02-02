package nbu.team11.controllers;

import jakarta.validation.Valid;
import nbu.team11.controllers.forms.CreateEmployeeForm;
import nbu.team11.controllers.forms.EmployeeForm;
import nbu.team11.controllers.forms.ui.SelectUtil;
import nbu.team11.dtos.EmployeeDto;
import nbu.team11.dtos.OfficeDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.entities.Employee;
import nbu.team11.entities.enums.PositionType;
import nbu.team11.services.EmployeeService;
import nbu.team11.services.OfficeService;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.ResourceNotFound;
import nbu.team11.services.exceptions.UsernameNotAvailable;
import org.modelmapper.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import nbu.team11.services.CustomPermissionEvaluator;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final OfficeService officeService;

    @Autowired
    private CustomPermissionEvaluator customPermissionEvaluator;

    public EmployeeController(EmployeeService employeeService, OfficeService officeService) {
        this.employeeService = employeeService;
        this.officeService = officeService;
    }

    private String withAppLayout(Model model) {
        return "layouts/app";
    }

    @RequestMapping()
    public String index(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<EmployeeDto> employees = employeeService.paginate(page, size);

        model.addAttribute("title", "All Employees");
        model.addAttribute("content", "employee/index");
        model.addAttribute("employees", employees);

        return "layouts/app";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("title", "Create Employee");
        model.addAttribute("content", "employee/create");
        model.addAttribute("offices", SelectUtil.fromList(officeService.getAll(),
                officeDto -> String.valueOf(officeDto.getId()), OfficeDto::getTitle));
        model.addAttribute("positions", SelectUtil.fromEnum(PositionType.class));

        // If we were redirected from the store method, we should have the form in the
        // model
        // including its errors
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new CreateEmployeeForm());
        }

        return "layouts/app";
    }

    @GetMapping("/manage")
    public String manageUsers(Model model, Authentication authentication) {
        if (!this.customPermissionEvaluator.hasRoleAndPosition(authentication, "EMPLOYEE", "Administrator")) {
            return "redirect:/home";
        }

        List<Employee> employees = employeeService.getAllEmployees();

        model.addAttribute("employees", employees);
        model.addAttribute("content", "user/admin/manage-employees");
        return withAppLayout(model);
    }

    @PostMapping("/store")
    public String store(@Valid CreateEmployeeForm createEmployeeForm, BindingResult bindingResult,
            RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            attributes.addFlashAttribute("form", createEmployeeForm);
            return "redirect:/employee/create";
        }

        try {
            employeeService.create(createEmployeeForm.toEmployeeDto(), createEmployeeForm.toUserDto());
        } catch (UsernameNotAvailable | EmailNotAvailable e) {
            switch (e.getClass().getSimpleName()) {
                case "UsernameNotAvailable" -> bindingResult.rejectValue("username", "error.username", e.getMessage());
                case "EmailNotAvailable" -> bindingResult.rejectValue("email", "error.email", e.getMessage());
            }
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            attributes.addFlashAttribute("form", createEmployeeForm);
            return "redirect:/employee/create";
        }
        attributes.addFlashAttribute("successMessage", "Employee created successfully");
        return "redirect:/employee";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable String id, Model model) {
        EmployeeDto employee = null;
        try {
            employee = employeeService.getById(Integer.parseInt(id));
        } catch (ResourceNotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found");
        }

        model.addAttribute("title", employee.getUserFullName() + " - View Employee");
        model.addAttribute("content", "employee/view");
        model.addAttribute("employee", employee);

        return "layouts/app";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
        EmployeeDto employee = null;
        try {
            employee = employeeService.getById(Integer.parseInt(id));
        } catch (ResourceNotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found");
        }

        EmployeeForm employeeForm = new EmployeeForm();
        employeeForm.setFirstName(employee.getUserFirstName());
        employeeForm.setLastName(employee.getUserLastName());
        employeeForm.setEmail(employee.getUserEmail());
        employeeForm.setUsername(employee.getUserUsername());
        employeeForm.setOfficeId(employee.getOfficeId());
        employeeForm.setPosition(employee.getPosition());

        model.addAttribute("title", employee.getUserFullName() + " - Edit Employee");
        model.addAttribute("content", "employee/edit");
        model.addAttribute("employee", employee);
        model.addAttribute("offices", SelectUtil.fromList(officeService.getAll(),
                officeDto -> String.valueOf(officeDto.getId()), OfficeDto::getTitle));
        model.addAttribute("positions", SelectUtil.fromEnum(PositionType.class));

        if (!model.containsAttribute("form")) {
            model.addAttribute("form", employeeForm);
        }

        return "layouts/app";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable String id, @Valid EmployeeForm updateEmployeeForm, BindingResult bindingResult,
            RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            attributes.addFlashAttribute("form", updateEmployeeForm);
            return "redirect:/employee/" + id + "/edit";
        }

        try {
            EmployeeDto employeeDto = this.employeeService.getById(Integer.parseInt(id));

            UserDto userDto = updateEmployeeForm.toUserDto();
            userDto.setId(employeeDto.getUserId());
            employeeService.update(employeeDto, userDto);
        } catch (UsernameNotAvailable | EmailNotAvailable | ResourceNotFound e) {
            switch (e.getClass().getSimpleName()) {
                case "UsernameNotAvailable" -> bindingResult.rejectValue("username", "error.username", e.getMessage());
                case "EmailNotAvailable" -> bindingResult.rejectValue("email", "error.email", e.getMessage());
                case "ResourceNotFound" -> throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "entity not found");
            }
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            attributes.addFlashAttribute("form", updateEmployeeForm);
            return "redirect:/employee/" + id + "/edit";
        }

        attributes.addFlashAttribute("successMessage", "Employee updated successfully");
        return "redirect:/employee/" + id;
    }
}
