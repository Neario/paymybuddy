package io.project.paymybuddy.web.controller;

import io.project.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Transactional
public class LoginControllerTestIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldDisplayRegisterForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("/auth/register"))
                .andExpect(model().attributeExists("userRegister"));
    }

    @Test
    void shouldDisplayLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    public void shouldRegisterUser() throws Exception {
        mockMvc.perform(post("/register")
                .param("username","mika")
                .param("email", "mika@test.com")
                .param("password", "mika")
                .with(csrf())
        ).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/auth/login"));
        assertThat(userRepository.findByEmail("mika@test.com")).isPresent();
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "mika")
                        .param("email", "mika@test.com")
                        .param("password", "mika")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/register")
                        .param("username", "mika2")
                        .param("email", "mika@test.com")
                        .param("password", "mika2")
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).isEqualTo("Email already exist"));

    }
}
