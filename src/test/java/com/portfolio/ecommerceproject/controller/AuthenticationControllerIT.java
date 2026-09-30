package com.portfolio.ecommerceproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.ecommerceproject.domain.model.Role;
import com.portfolio.ecommerceproject.domain.model.User;
import com.portfolio.ecommerceproject.domain.repository.RoleRepository;
import com.portfolio.ecommerceproject.domain.repository.UserRepository;
import com.portfolio.ecommerceproject.dto.AuthenticationDto;
import com.portfolio.ecommerceproject.dto.RegisterDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AuthenticationControllerIT {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    private String userEmail;
    private String userPassword;
    private Role roleClient, roleAdmin;

    @BeforeEach
    void setUp() {
        userEmail = "user.test@email.com";
        userPassword = "password123";

        roleClient = roleRepository.findByAuthority("ROLE_CLIENT")
                .orElseGet(() -> roleRepository.save(new Role(1L, "ROLE_CLIENT")));

        roleAdmin = roleRepository.findByAuthority("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(1L, "ROLE_ADMIN")));

        if(userRepository.findByEmail(userEmail).isEmpty()) {
            String encryptedPassword = new BCryptPasswordEncoder().encode(userPassword);
            User user = new User(1L, "name", userEmail, encryptedPassword);
            user.addRole(roleClient);
            userRepository.save(user);
        }
    }

    @Nested
    @DisplayName("POST /auth/register - User register")
    class RegisterUser {
        @Test
        @DisplayName("Should register a new user and returns status 200/201")
        void shouldRegisterUserAndReturnStatus200() throws Exception {
            RegisterDto registerDto = new RegisterDto("name","new.user@email.com", "password123", false);

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerDto)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should return 400 bad request when registering existing email")
        void shouldReturn400BadRequestWhenRegisteringExistingEmail() throws Exception {
            RegisterDto registerDto = new RegisterDto("name",userEmail, "password123", false);

            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /auth/login - Authentication and token generating")
    class Login {
        @Test
        @DisplayName("Should authenticate with valid credentials and return jwt token")
        void  shouldAuthenticateWithValidCredentialsAndReturnJwtToken() throws Exception {
            AuthenticationDto authenticationDto = new AuthenticationDto(userEmail, userPassword);

            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authenticationDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("Should return error when authentication with invalid credentials")
        void shouldReturnErrorWhenAuthenticationWithInvalidCredentials() throws Exception {
            AuthenticationDto authenticationDto = new AuthenticationDto(userEmail, "invalidPassword");

            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(authenticationDto)))
                    .andExpect(status().isUnauthorized());
        }
    }

}
