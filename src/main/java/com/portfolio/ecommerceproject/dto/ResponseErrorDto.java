package com.portfolio.ecommerceproject.dto;

import java.time.Instant;
import java.util.List;

public record ResponseErrorDto(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        String path,
        List<InvalidField> fields
) {

    public record InvalidField(String field, String message) {}

}
