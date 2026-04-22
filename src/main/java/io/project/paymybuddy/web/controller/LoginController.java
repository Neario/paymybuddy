package io.project.paymybuddy.web.controller;

import io.project.paymybuddy.dto.UserRegisterDto;
import io.project.paymybuddy.service.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        model.addAttribute("userRegister", userRegisterDto);
        return "/auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userRegister") UserRegisterDto userRegisterDto, BindingResult result, Model model) {
        userService.createUser(userRegisterDto);
        return "redirect:/auth/login";
    }
}
