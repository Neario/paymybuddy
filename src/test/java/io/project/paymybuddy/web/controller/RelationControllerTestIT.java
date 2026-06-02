package io.project.paymybuddy.web.controller;

import io.project.paymybuddy.model.User;
import io.project.paymybuddy.model.enumeration.UserRole;
import io.project.paymybuddy.repository.RelationRepository;
import io.project.paymybuddy.repository.UserRepository;
import io.project.paymybuddy.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Transactional
public class RelationControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RelationRepository relationRepository;

    private User currentUser;
    private User contact;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setUsername("mika");
        currentUser.setPassword("mika");
        currentUser.setEmail("mika@test.com");
        currentUser.setRole(UserRole.USER);
        userRepository.save(currentUser);

        contact = new User();
        contact.setUsername("test");
        contact.setPassword("test");
        contact.setEmail("test@test.com");
        contact.setRole(UserRole.USER);
        userRepository.save(contact);

        userDetails = new CustomUserDetails(currentUser);
    }

    @Test
    void shouldDisplayContactForm() throws Exception {
        mockMvc.perform(get("/contact").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("relation/addContact"))
                .andExpect(model().attributeExists("relation"));
    }

    @Test
    void shouldAddContactAndRedirect() throws Exception {
        mockMvc.perform(post("/contact")
                        .param("email", "test@test.com")
                        .with(csrf())
                        .with(user(userDetails)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact/show"));
    }

    @Test
    void shouldAddContactAndUserNotFound() throws Exception {
        mockMvc.perform(post("/contact")
                        .param("email", "try@test.com")
                        .with(csrf())
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("relation/addContact"))
                .andExpect(model().attributeExists("errors"))
                .andExpect(model().attribute("errors", contains("No user found with email")));
    }

    @Test
    void shouldAddContactAndYourself() throws Exception {
        mockMvc.perform(post("/contact")
                        .param("email", "mika@test.com")
                        .with(csrf())
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("relation/addContact"))
                .andExpect(model().attributeExists("errors"))
                .andExpect(model().attribute("errors", contains("Cannot add yourself")));
    }

}
