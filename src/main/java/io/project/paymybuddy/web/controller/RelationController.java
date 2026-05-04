package io.project.paymybuddy.web.controller;

import io.project.paymybuddy.dto.RelationDto;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.security.CustomUserDetails;
import io.project.paymybuddy.service.interfaces.RelationService;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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
    public String addContact(@Valid @ModelAttribute("relation") RelationDto relationDto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal CustomUserDetails currentUser, Model model) {

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();

            model.addAttribute("errors", errors);
            model.addAttribute("relation", relationDto);

            return "relation/addContact";
        }

        try {
            relationService.addRelation(currentUser.getUser(), relationDto);
            return "redirect:/contact/show";
        } catch (Exception exception) {
            model.addAttribute("errors", List.of(exception.getMessage()));
            model.addAttribute("relation", relationDto);
            return "relation/addContact";

        }
    }
}
