package nbu.team11;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// uncomment if you want to return a string/json
//import org.springframework.web.bind.annotation.RestController;


// uncomment if you want to return a string/json
//@RestController
@Controller
@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = {"nbu.team11", "entities", "configurations", "controllers", "repositories", "services"})
public class MyApplication {

    @GetMapping("/")
    String index() {
        return "index";
    }

    @GetMapping("/hello")
    String hello(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "hello";

    }

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}