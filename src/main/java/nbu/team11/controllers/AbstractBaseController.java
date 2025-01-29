package nbu.team11.controllers;

import nbu.team11.services.CustomPermissionEvaluator;
import org.springframework.stereotype.Controller;

@Controller
abstract public class AbstractBaseController {

    protected final CustomPermissionEvaluator permissionEvaluator;

    public AbstractBaseController(CustomPermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }
}
