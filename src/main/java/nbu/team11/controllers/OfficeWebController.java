package nbu.team11.controllers;

import jakarta.validation.Valid;
import nbu.team11.controllers.forms.CreateOfficeForm;
import nbu.team11.dtos.OfficeDto;
import nbu.team11.services.CustomPermissionEvaluator;
import nbu.team11.services.OfficeService;
import nbu.team11.services.exceptions.ResourceNotFound;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/office")
@PreAuthorize("@customPermissionEvaluator.isAdmin(authentication)")
public class OfficeWebController extends AbstractBaseController {

    private final OfficeService officeService;

    public OfficeWebController(CustomPermissionEvaluator permissionEvaluator, OfficeService officeService) {
        super(permissionEvaluator);
        this.officeService = officeService;
    }

    @GetMapping
    public String index(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search
    ) {
        Page<OfficeDto> offices = officeService.paginate(page, size, search);

        model.addAttribute("title", "All Offices");
        model.addAttribute("content", "office/index");
        model.addAttribute("offices", offices);
        model.addAttribute("search", search);

        return "layouts/app";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("title", "Create Office");
        model.addAttribute("content", "office/create");

        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new CreateOfficeForm());
        }

        return "layouts/app";
    }

    @PostMapping("/store")
    public String store(
            @Valid CreateOfficeForm form,
            BindingResult bindingResult,
            RedirectAttributes attributes
    ) {
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            attributes.addFlashAttribute("form", form);
            return "redirect:/office/create";
        }

        officeService.create(form.getTitle(), form.getFormattedAddress(), form.getCoordinates());

        attributes.addFlashAttribute("successMessage", "Office created successfully");
        return "redirect:/office";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        OfficeDto office;
        try {
            office = officeService.getById(id);
        } catch (ResourceNotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Office not found");
        }

        model.addAttribute("title", "Edit Office - " + office.getTitle());
        model.addAttribute("content", "office/edit");
        model.addAttribute("office", office);

        if (!model.containsAttribute("form")) {
            CreateOfficeForm form = new CreateOfficeForm();
            form.setTitle(office.getTitle());
            if (office.getAddressDto() != null) {
                form.setFormattedAddress(office.getAddressDto().getFormattedAddress());
                form.setCoordinates(office.getAddressDto().getCoordinates());
            }
            model.addAttribute("form", form);
        }

        return "layouts/app";
    }

    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Integer id,
            @Valid CreateOfficeForm form,
            BindingResult bindingResult,
            RedirectAttributes attributes
    ) {
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("org.springframework.validation.BindingResult.form", bindingResult);
            attributes.addFlashAttribute("form", form);
            return "redirect:/office/" + id + "/edit";
        }

        try {
            officeService.update(id, form.getTitle(), form.getFormattedAddress(), form.getCoordinates());
        } catch (ResourceNotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Office not found");
        }

        attributes.addFlashAttribute("successMessage", "Office updated successfully");
        return "redirect:/office";
    }
}
