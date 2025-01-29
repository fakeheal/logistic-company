package nbu.team11.controllers;

import nbu.team11.dtos.UserDto;
import nbu.team11.entities.enums.Role;
import nbu.team11.services.CustomPermissionEvaluator;
import nbu.team11.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClientController extends AbstractBaseController {

    private final UserService userService;

    public ClientController(CustomPermissionEvaluator permissionEvaluator, UserService userService) {
        super(permissionEvaluator);
        this.userService = userService;
    }

    @RequestMapping("/client")
    public String index(
            Model model,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (!this.permissionEvaluator.isAdmin(authentication)) {
            return "redirect:/home";
        }

        Page<UserDto> clients = userService.paginate(page, size, Role.CLIENT);

        model.addAttribute("title", "All clients");
        model.addAttribute("content", "client/index");
        model.addAttribute("clients", clients);

        return "layouts/app";
    }
}
