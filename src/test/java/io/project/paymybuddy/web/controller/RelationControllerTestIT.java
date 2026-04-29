package io.project.paymybuddy.web.controller;

import io.project.paymybuddy.exception.UserNotFoundException;
import io.project.paymybuddy.model.User;
import io.project.paymybuddy.model.enumeration.UserRole;
import io.project.paymybuddy.security.CustomUserDetails;
import io.project.paymybuddy.service.interfaces.RelationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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

    @MockitoBean
    private RelationService relationService;

    private User currentUser;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("mika");
        currentUser.setEmail("mika@test.com");
        currentUser.setRole(UserRole.USER);

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

// TEST EXCEPTION
}
