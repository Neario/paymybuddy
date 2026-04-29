package io.project.paymybuddy.web.controller;

import io.project.paymybuddy.dto.RelationDto;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.security.CustomUserDetails;
import io.project.paymybuddy.service.interfaces.RelationService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RelationController {

    private final RelationService relationService;

    public RelationController(RelationService relationService) {
        this.relationService = relationService;
    }

    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("relation", new RelationDto());
        return "relation/addContact";
    }

    @GetMapping("/contact/show")
    public String showContacts(Model model, @AuthenticationPrincipal CustomUserDetails currentUser) {
        model.addAttribute("user", currentUser.getUser());
        model.addAttribute("relations", relationService.getRelations(currentUser.getUser().getId()));
        return "relation/showContacts";
    }

    @PostMapping("/contact")
    public String addContact(@Valid @ModelAttribute RelationDto relationDto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal CustomUserDetails currentUser) {
        relationService.addRelation(currentUser.getUser(), relationDto);
        return "redirect:/contact/show";
    }
}
