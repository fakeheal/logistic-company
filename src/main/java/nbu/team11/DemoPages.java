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
        model.addAttribute("content", "demo/index");
        return withAppLayout(model);
    }

    @GetMapping("/demo/login")
    public String login(Model model) {
        model.addAttribute("title", "Login Page");
        model.addAttribute("content", "demo/login");
        return withAppLayout(model);
    }

    @GetMapping("/demo/register")
    public String register(Model model) {
        model.addAttribute("title", "Register Page");
        model.addAttribute("content", "demo/register");
        return withAppLayout(model);
    }

    @GetMapping("/demo/forgotten-password")
    public String forgottenPassword(Model model) {
        model.addAttribute("title", "Forgotten password");
        model.addAttribute("content", "demo/forgotten-password");
        return withAppLayout(model);
    }

    @GetMapping("/demo/reset-password")
    public String resetPassword(Model model) {
        model.addAttribute("title", "Reset password");
        model.addAttribute("content", "demo/reset-password");
        return withAppLayout(model);
    }

    @GetMapping("/demo/track")
    public String track(Model model) {
        model.addAttribute("title", "Track package");
        model.addAttribute("content", "demo/track");
        return withAppLayout(model);
    }

    @GetMapping("/demo/contact")
    public String privacyPolicy(Model model) {
        model.addAttribute("title", "Contact");
        model.addAttribute("content", "demo/contact");
        return withAppLayout(model);
    }
}
