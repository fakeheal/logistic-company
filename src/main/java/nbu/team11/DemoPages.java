package nbu.team11;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoPages {

    private String withAppLayout(Model model) {
        return "demo/layouts/app";
    }

    @GetMapping("/demo")
    public String demo(Model model) {
        model.addAttribute("title", "Demo Page");
        model.addAttribute("content", "demo/pages/index");
        return withAppLayout(model);
    }

    @GetMapping("/demo/login")
    public String login(Model model) {
        model.addAttribute("title", "Login Page");
        model.addAttribute("content", "demo/pages/guest/login");
        return withAppLayout(model);
    }

    @GetMapping("/demo/register")
    public String register(Model model) {
        model.addAttribute("title", "Register Page");
        model.addAttribute("content", "demo/pages/guest/register");
        return withAppLayout(model);
    }

    @GetMapping("/demo/forgotten-password")
    public String forgottenPassword(Model model) {
        model.addAttribute("title", "Forgotten password");
        model.addAttribute("content", "demo/pages/guest/forgotten-password");
        return withAppLayout(model);
    }

    @GetMapping("/demo/reset-password")
    public String resetPassword(Model model) {
        model.addAttribute("title", "Reset password");
        model.addAttribute("content", "demo/pages/guest/reset-password");
        return withAppLayout(model);
    }

    @GetMapping("/demo/track")
    public String track(Model model) {
        model.addAttribute("title", "Track package");
        model.addAttribute("content", "demo/pages/track");
        return withAppLayout(model);
    }

    @GetMapping("/demo/contact")
    public String privacyPolicy(Model model) {
        model.addAttribute("title", "Contact");
        model.addAttribute("content", "demo/pages/contact");
        return withAppLayout(model);
    }

    @GetMapping("/demo/employee")
    public String employee(Model model) {
        model.addAttribute("title", "All employees");
        model.addAttribute("content", "demo/pages/employee/index");
        return withAppLayout(model);
    }

    @GetMapping("/demo/employee/create")
    public String createEmployee(Model model) {
        model.addAttribute("title", "Create employee");
        model.addAttribute("content", "demo/pages/employee/create");
        return withAppLayout(model);
    }

    @GetMapping("/demo/employee/view")
    public String viewEmployee(Model model) {
        model.addAttribute("title", "View employee");
        model.addAttribute("content", "demo/pages/employee/view");
        return withAppLayout(model);
    }
}
