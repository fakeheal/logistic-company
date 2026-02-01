package nbu.team11.controllers;

import nbu.team11.dtos.OfficeDto;
import nbu.team11.services.CustomPermissionEvaluator;
import nbu.team11.services.OfficeService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
