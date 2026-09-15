package com.portfolio.ecommerceproject.controller;

import com.portfolio.ecommerceproject.dto.ProductDto;
import com.portfolio.ecommerceproject.dto.ProductResponseDto;
import com.portfolio.ecommerceproject.service.CategoryService;
import com.portfolio.ecommerceproject.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    private ProductService productService;
    private CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductDto dto) {
        ProductResponseDto productResponseDto = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDto);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<ProductResponseDto> page = productService.getAllProducts(pageable);
        return ResponseEntity.ok(page);
    }
}
