package com.portfolio.ecommerceproject.controller;

import com.portfolio.ecommerceproject.dto.CategoryDto;
import com.portfolio.ecommerceproject.dto.CategoryResponseDto;
import com.portfolio.ecommerceproject.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryDto dto) {
        CategoryResponseDto categoryResponseDto = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseDto);
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponseDto>> getCategories(
            @PageableDefault(page = 0, size = 10, sort = "name")Pageable pageable
            ) {
        Page<CategoryResponseDto> categories = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(categories);
    }
}
