package nbu.team11.controllers;
import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nbu.team11.entities.enums.Role;
import nbu.team11.services.CustomPermissionEvaluator;
import nbu.team11.services.CustomUserDetails;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.UsernameNotAvailable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.oauth2.resourceserver.OAuth2ResourceServerSecurityMarker;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import nbu.team11.dtos.UserDto;
import nbu.team11.services.UserService;
import nbu.team11.entities.*;
@Controller
public class UserController {

    @Autowired
    private CustomPermissionEvaluator customPermissionEvaluator;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;

    private UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    private String withAppLayout(Model model) {
        return "layouts/app";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("content", "index");
        return withAppLayout(model);
    }

    @GetMapping("/manage-users")
    public String manageUsers(Model model, Authentication authentication) {
        if (!this.customPermissionEvaluator.hasRoleAndPosition(authentication,"EMPLOYEE", "Administrator")) {
            return "redirect:/home";
        }

        model.addAttribute("content", "user/admin/manage-users");
        return withAppLayout(model);
    }

    @GetMapping("/login")
    public String login(Model model, UserDto userDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }

        model.addAttribute("user", userDto);
        model.addAttribute("title", "Login");
        model.addAttribute("content", "user/login");
        return withAppLayout(model);
    }

    @GetMapping("/register")
    public String register(Model model, UserDto userDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/demo";
        }

        model.addAttribute("user", userDto);
        model.addAttribute("title", "Register");
        model.addAttribute("content", "user/register");
        return withAppLayout(model);
    }

    @PostMapping("/register")
    public String registerSave(@ModelAttribute("user") UserDto userDto, Model model, HttpServletRequest request) {
        UserDto matchedUser = userService.getByUsername(userDto.getUsername());
        if (matchedUser != null) {
            model.addAttribute("userexist", matchedUser);
            return "user/register";
        }
        userDto.setRole(Role.CLIENT);
        try {
            userService.create(userDto);
        } catch (UsernameNotAvailable | EmailNotAvailable e) {
            // @TODO: Handle exception
            throw new RuntimeException(e);
        }
        authenticateUserAndSetSession(userDto, request);
        return "redirect:/home";
    }

    private void authenticateUserAndSetSession(UserDto user, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetails(request));
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}