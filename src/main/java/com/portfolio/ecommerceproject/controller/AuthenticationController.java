package com.portfolio.ecommerceproject.controller;

import com.portfolio.ecommerceproject.domain.model.Role;
import com.portfolio.ecommerceproject.domain.model.User;
import com.portfolio.ecommerceproject.domain.repository.RoleRepository;
import com.portfolio.ecommerceproject.domain.repository.UserRepository;
import com.portfolio.ecommerceproject.dto.AuthenticationDto;
import com.portfolio.ecommerceproject.dto.LoginResponseDto;
import com.portfolio.ecommerceproject.dto.RegisterDto;
import com.portfolio.ecommerceproject.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Login and register endpoints")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Operation(summary = "Executes login and returns JWT Token")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody AuthenticationDto data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @Operation(summary = "Registers a new user")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterDto data) {
        if(userRepository.existsByEmail(data.email())) {
            return ResponseEntity.badRequest().build();
        }
        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(null, data.name(), data.email(), encryptedPassword);

        String roleName = data.isAdmin() ? "ROLE_ADMIN": "ROLE_USER";
        Role role = roleRepository.findByAuthority(roleName)
                .orElseGet(() -> roleRepository.save(new Role(null, roleName)));

        newUser.addRole(role);
        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
