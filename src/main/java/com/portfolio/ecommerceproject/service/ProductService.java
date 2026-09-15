package com.portfolio.ecommerceproject.service;

import com.portfolio.ecommerceproject.domain.model.Category;
import com.portfolio.ecommerceproject.domain.model.Product;
import com.portfolio.ecommerceproject.domain.repository.CategoryRepository;
import com.portfolio.ecommerceproject.domain.repository.ProductRepository;
import com.portfolio.ecommerceproject.dto.CategoryResponseDto;
import com.portfolio.ecommerceproject.dto.ProductDto;
import com.portfolio.ecommerceproject.dto.ProductResponseDto;
import com.portfolio.ecommerceproject.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ProductResponseDto createProduct(ProductDto productDto) {
        Category category = categoryRepository.findById(productDto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = new Product();
        product.setName(productDto.name());
        product.setDescription(productDto.description());
        product.setPrice(productDto.price());
        product.setQuantity(productDto.quantity());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return productToDto(savedProduct);
    }

    @Transactional
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(product -> productToDto(product));
    }

    private ProductResponseDto productToDto(Product product) {
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto(
                product.getCategory().getId(),
                product.getCategory().getName()
        );

        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                categoryResponseDto
        );

    }
}
