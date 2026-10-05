package com.portfolio.ecommerceproject.service;

import com.portfolio.ecommerceproject.domain.model.User;
import com.portfolio.ecommerceproject.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class TokenServiceTest {
    private TokenService tokenService;
    private User user;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        //Injects the application.properties secret via reflection
        ReflectionTestUtils.setField(tokenService, "secret", "my-secret-s@afest!-test-key-12345");
        ReflectionTestUtils.setField(tokenService, "expiration", 7200000L);

        user = new User();
        user.setEmail("user@email.com");
        user.setPassword("123456");
    }

    @Test
    @DisplayName("Should generate a valid JWT token")
    void generateToken() {
        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Should validate JWT token and return email as subject")
    void validateTokenAndUserAsSubject() {
        String token = tokenService.generateToken(user);
        String subject = tokenService.validateTokenAndGetSubject(token);

        assertEquals(user.getEmail(), subject);
    }

    @Test
    @DisplayName("Should return an empty string or null when invalid jwt token")
    void validateTokenAndUserAsInvalidJwt() {
        String token = "token.jwt.invalid";
        String subject = tokenService.validateTokenAndGetSubject(token);

        assertTrue(subject == null || subject.isEmpty());
    }
}
