package com.portfolio.ecommerceproject.controller;

import com.portfolio.ecommerceproject.dto.ProductDto;
import com.portfolio.ecommerceproject.dto.ProductResponseDto;
import com.portfolio.ecommerceproject.service.CategoryService;
import com.portfolio.ecommerceproject.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/products")
@Tag(name = "Products", description = "Endpoints to manage products")
public class ProductController {
    final private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Adds a new product", description = "Creates a new product from sent data")
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductDto dto) {
        ProductResponseDto productResponseDto = productService.createProduct(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productResponseDto.id())
                .toUri();

        return ResponseEntity.created(location).body(productResponseDto);
    }

    @Operation(summary = "Gets a category by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok().body(productService.findProductById(id));
    }

    @Operation(summary = "Lists all categories paginated with optional filtering by name and categoryId", description = "Returns a paginated products list")
    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
    ) {
        Page<ProductResponseDto> page = productService.getAllProducts(name, categoryId, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Updates an existing product by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProductById(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok().body(productService.updateProductById(id, dto));
    }

    @Operation(summary = "Deletes an existing product by ID")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
}
