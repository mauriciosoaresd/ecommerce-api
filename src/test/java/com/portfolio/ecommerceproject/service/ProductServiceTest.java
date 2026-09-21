package com.portfolio.ecommerceproject.service;

import com.portfolio.ecommerceproject.domain.model.Category;
import com.portfolio.ecommerceproject.domain.model.Product;
import com.portfolio.ecommerceproject.domain.repository.CategoryRepository;
import com.portfolio.ecommerceproject.domain.repository.ProductRepository;
import com.portfolio.ecommerceproject.dto.CategoryResponseDto;
import com.portfolio.ecommerceproject.dto.ProductDto;
import com.portfolio.ecommerceproject.dto.ProductResponseDto;
import com.portfolio.ecommerceproject.mapper.ProductMapper;
import com.portfolio.ecommerceproject.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private ProductService productService;

    private Category category;
    private Product product;
    private ProductDto productDto;
    private ProductResponseDto productResponseDto;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Eletronics");
        productDto = new ProductDto(
                "Smartphone",
                "New Gen Smartphone",
                new BigDecimal("2500.99"),
                10,
                1L);

        product = new Product();
        product.setId(1L);
        product.setName("Smartphone");
        product.setDescription("New Gen Smartphone");
        product.setPrice(new BigDecimal("2500.99"));
        product.setQuantity(10);
        product.setCategory(category);

        productResponseDto = new ProductResponseDto(
                1L,
                "Smartphone",
                "New Gen Smartphone",
                new BigDecimal("2500.99"),
                10,
                new CategoryResponseDto(1L, "Eletronics")
        );
    }

    @Nested
    @DisplayName("Create product scenario")
    class CreateProduct {
        @Test
        @DisplayName("Should create a product successfully when category exists")
        void shouldCreateProductSuccessfullyWhenCategoryExists() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(productMapper.toEntity(productDto)).thenReturn(product);
            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(productMapper.toDto(product)).thenReturn(productResponseDto);

            ProductResponseDto result = productService.createProduct(productDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Smartphone");
            assertThat(result.category().id()).isEqualTo(1L);

            verify(categoryRepository, times(1)).findById(1L);
            verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFound when creating product with inexistent category")
        void shouldThrowResourceNotFoundWhenCategoryDoesNotExist() {
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            ProductDto dtoWithInexistentCategory = new ProductDto(
                    "Smartphone",
                    "New Gen Smartphone",
                    new BigDecimal("2500.99"),
                    5,
                    99L
            );

            assertThatThrownBy(() -> productService.createProduct(dtoWithInexistentCategory))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Category not found");

            verify(categoryRepository, times(1)).findById(99L);
            verify(productRepository, never()).save(any(Product.class));
            verifyNoInteractions(productMapper);
        }
    }

    @Nested
    @DisplayName("Find by ID scenarios")
    class FindById {
        @Test
        @DisplayName("Should return ProductResponseDto when product exists")
        void shouldReturnProductResponseDtoWhenProductExists() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productMapper.toDto(product)).thenReturn(productResponseDto);

            ProductResponseDto result = productService.findProductById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            verify(productRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFound when product inexists")
        void shouldThrowResourceNotFoundWhenProductDoesNotExist() {
            when(productRepository.findById(199L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.findProductById(199L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found");

            verify(productRepository, times(1)).findById(199L);
            verifyNoInteractions(productMapper);
        }
    }

    @Nested
    @DisplayName("Deleting product scenarios")
    class DeleteById {
        @Test
        @DisplayName("Should successfully delete when product ID exists")
        void shouldDeleteProductSuccessfullyWhenProductExists() {
            when(productRepository.existsById(1L)).thenReturn(true);
            doNothing().when(productRepository).deleteById(1L);

            productService.deleteProductById(1L);

            verify(productRepository, times(1)).existsById(1L);
            verify(productRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFound when deleting inexistent product ID")
        void shouldThrowResourceNotFoundWhenProductDoesNotExist() {
            when(productRepository.existsById(199L)).thenReturn(false);

            assertThatThrownBy(() -> productService.deleteProductById(199L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found");

            verify(productRepository, times(1)).existsById(199L);
            verify(productRepository, never()).deleteById(anyLong());
        }

    }
}
