package nbu.team11.controllers;
import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import nbu.team11.entities.enums.Role;
import nbu.team11.services.exceptions.EmailNotAvailable;
import nbu.team11.services.exceptions.UsernameNotAvailable;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;

    private UserService userService;
    public UserController(UserService userService) {

        this.userService = userService;
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        model.addAttribute("userdetail" , userDetails);
        return "home";
    }

    @GetMapping("/login")
    public String login(Model model, UserDto userDto) {
        model.addAttribute("user", userDto);
        return "login";
    }


    @GetMapping("/register")
    public String register(Model model, UserDto userDto) {
        model.addAttribute("user", userDto);
        return "register";
    }

    @PostMapping("/register")
    public String registerSave(@ModelAttribute("user") UserDto userDto, Model model, HttpServletRequest request) {
        UserDto matchedUser = userService.getByUsername(userDto.getUsername());
        if (matchedUser != null) {
            model.addAttribute("userexist", matchedUser);
            return "register";
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

    //NOT IMPLEMENTED - TODO:Login user after registration
    private void authenticateUserAndSetSession(UserDto user, HttpServletRequest request) {
        // Use raw password from userDto, not the encoded one
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

        authToken.setDetails(new WebAuthenticationDetails(request));

        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(authToken);

        // Set authentication in SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Set the session attribute to persist authentication
        //TODO:Check if can be removed
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
    }
}