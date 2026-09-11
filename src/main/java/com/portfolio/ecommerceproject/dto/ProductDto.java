package com.portfolio.ecommerceproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductDto(
        @NotBlank(message = "Name is required") String name,
        String description,
        @NotNull(message = "Price is required")
        @Positive(message = "Price must be a positive value") BigDecimal price,
        @NotNull(message = "Quantity is required")
        @PositiveOrZero(message = "Quantity must be a positive value") Integer quantity,
        @NotNull(message = "Category ID is required")
        Long categoryId
) {
}
