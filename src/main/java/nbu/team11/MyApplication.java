package nbu.team11;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@SpringBootApplication(scanBasePackages = {"nbu.team11", "entities", "configurations", "controllers", "repositories", "services"})
public class MyApplication {

    @GetMapping("/")
    String index(Model model) {
        model.addAttribute("title", "nbcont – Your Trusted Partner for Global Logistics");
        model.addAttribute("content", "index");
        return "layouts/app";
    }

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}