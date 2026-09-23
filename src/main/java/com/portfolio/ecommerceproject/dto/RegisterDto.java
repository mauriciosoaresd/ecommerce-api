package com.portfolio.ecommerceproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDto(
    @NotBlank(message = "Name is required")
    String name,
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must have 6 characters")
    String password,
    boolean isAdmin
){}
