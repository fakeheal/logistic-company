package nbu.team11.controllers;

import nbu.team11.dtos.ClientDto;
import nbu.team11.dtos.UserDto;
import nbu.team11.entities.enums.Role;
import nbu.team11.services.ClientService;
import nbu.team11.services.CustomPermissionEvaluator;
import nbu.team11.services.UserService;
import nbu.team11.services.exceptions.ResourceNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ClientController extends AbstractBaseController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final ClientService clientService;

    public ClientController(CustomPermissionEvaluator permissionEvaluator, UserService userService, ClientService clientService) {
        super(permissionEvaluator);
        this.userService = userService;
        this.clientService = clientService;
    }

    @RequestMapping("/api/clients/getAllClients")
    @GetMapping
    public List<ClientDto> getAllClients() {
        return clientService.getAllClients();
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

    @GetMapping("/api/client/current")
    @ResponseBody
    public ClientDto getCurrentClient(Authentication authentication)
            throws ResourceNotFound {
        return clientService.getClientByEmail(authentication.getName());
    }
}
