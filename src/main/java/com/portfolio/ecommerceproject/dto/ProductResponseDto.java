package com.portfolio.ecommerceproject.dto;

import java.math.BigDecimal;

public record ProductResponseDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        CategoryResponseDto category
) {
}
