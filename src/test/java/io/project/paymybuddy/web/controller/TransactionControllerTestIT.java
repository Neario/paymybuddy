package io.project.paymybuddy.web.controller;

import io.project.paymybuddy.model.Relation;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.model.Wallet;
import io.project.paymybuddy.model.enumeration.UserRole;
import io.project.paymybuddy.repository.RelationRepository;
import io.project.paymybuddy.repository.TransactionRepository;
import io.project.paymybuddy.repository.UserRepository;
import io.project.paymybuddy.security.CustomUserDetails;
import io.project.paymybuddy.service.interfaces.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Transactional
public class TransactionControllerTestIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private UserRepository userRepository;

    private User currentUser;
    private User contact;
    private Wallet senderWallet;
    private Wallet receiverWallet;
    private CustomUserDetails userDetails;

    @BeforeEach
    public void setup() {
        currentUser = new User();
        currentUser.setUsername("mika");
        currentUser.setEmail("mika@test.com");
        currentUser.setPassword("mika");
        currentUser.setRole(UserRole.USER);

        senderWallet = new Wallet();
        senderWallet.setUser(currentUser);
        senderWallet.setBalance(10000);
        currentUser.setWallet(senderWallet);

        userRepository.save(currentUser);

        contact = new User();
        contact.setUsername("test");
        contact.setPassword("test");
        contact.setEmail("test@test.com");
        contact.setRole(UserRole.USER);

        receiverWallet = new Wallet();
        receiverWallet.setUser(contact);
        receiverWallet.setBalance(10000);
        contact.setWallet(receiverWallet);

        userRepository.save(contact);

        Relation relation = new Relation();
        relation.setUser(currentUser);
        relation.setRelation(contact);
        relationRepository.save(relation);

        userDetails = new CustomUserDetails(currentUser);
    }

    @Test
    public void shouldReturnTransactionPageWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/transaction").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction/index"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attributeExists("transactionRequest"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attributeExists("contacts"));
    }

    @Test
    public void shouldRedirectToLoginWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/transaction"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void shouldRedirectToTransactionWhenTransactionSuccess() throws Exception {
        mockMvc.perform(post("/transaction").with(user(userDetails))
                        .with(csrf())
                        .param("receiver", "test@test.com")
                        .param("amount", "10")
                        .param("description", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transaction"));
    }

    @Test
    public void shouldReturnTransactionPageWhenValidationError() throws Exception {
        mockMvc.perform(post("/transaction").with(user(userDetails))
                        .with(csrf())
                        .param("receiver", "")
                        .param("amount", "0")
                        .param("description", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("transaction/index"))
                .andExpect(model().attributeExists("errors"));
    }
}
