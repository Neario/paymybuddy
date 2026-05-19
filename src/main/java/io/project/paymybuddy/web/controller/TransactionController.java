package io.project.paymybuddy.web.controller;


import io.project.paymybuddy.dto.TransactionRequestDto;
import io.project.paymybuddy.security.CustomUserDetails;
import io.project.paymybuddy.service.interfaces.RelationService;
import io.project.paymybuddy.service.interfaces.TransactionService;
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
public class TransactionController {

    private final TransactionService transactionService;
    private final RelationService relationService;

    public TransactionController(TransactionService transactionService, RelationService relationService) {
        this.transactionService = transactionService;
        this.relationService = relationService;
    }

    @GetMapping("/transaction")
    public String showTransaction(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        hydrateModel(currentUser, model, new TransactionRequestDto());
        return "transaction/index";
    }

    @PostMapping("/transaction")
    public String sendCash(@Valid @ModelAttribute("transactionRequest") TransactionRequestDto transactionRequestDto,
                           BindingResult bindingResult,
                           @AuthenticationPrincipal CustomUserDetails currentUser,
                           Model model) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            model.addAttribute("errors", errors);
            hydrateModel(currentUser, model, transactionRequestDto);
            return "transaction/index";
        }
        try {
            transactionService.transaction(currentUser.getUser(), transactionRequestDto);
            return "redirect:/transaction";
        } catch (Exception e) {
            model.addAttribute("errors", List.of(e.getMessage()));
            hydrateModel(currentUser, model, transactionRequestDto);
            return "transaction/index";
        }
    }

    private void hydrateModel(CustomUserDetails currentUser, Model model, TransactionRequestDto transactionRequestDto) {
        model.addAttribute("currentUser", currentUser.getUser());
        model.addAttribute("transactionRequest", transactionRequestDto);
        model.addAttribute("transactions", transactionService.getTransactions(currentUser.getUser().getId()));
        model.addAttribute("contacts", relationService.getRelations(currentUser.getUser().getId()));
    }
}
