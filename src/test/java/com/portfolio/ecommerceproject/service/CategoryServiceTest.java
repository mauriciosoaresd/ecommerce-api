package com.portfolio.ecommerceproject.service;

import com.portfolio.ecommerceproject.domain.model.Category;
import com.portfolio.ecommerceproject.domain.repository.CategoryRepository;
import com.portfolio.ecommerceproject.dto.CategoryDto;
import com.portfolio.ecommerceproject.dto.CategoryResponseDto;
import com.portfolio.ecommerceproject.mapper.CategoryMapper;
import com.portfolio.ecommerceproject.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDto categoryDto;
    private CategoryResponseDto categoryResponseDto;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Eletronics");
        categoryDto = new CategoryDto("Eletronics");
        categoryResponseDto = new CategoryResponseDto(1L, "Eletronics");
    }

    @Nested
    @DisplayName("Creating category scenario")
    class CreateCategory {
        @Test
        @DisplayName("Should create a category successfully")
        void shouldCreateCategorySuccessfully() {
            when(categoryMapper.toEntity(categoryDto)).thenReturn(category);
            when(categoryRepository.save(any(Category.class))).thenReturn(category);
            when(categoryMapper.toDto(category)).thenReturn(categoryResponseDto);

            CategoryResponseDto result = categoryService.createCategory(categoryDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Eletronics");

            verify(categoryRepository, times(1)).save(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Find by ID scenarios")
    class FindById {
        @Test
        @DisplayName("Should return category successfully when ID exists")
        void shouldReturnCategorySuccessfullyWhenIdExists() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryMapper.toDto(category)).thenReturn(categoryResponseDto);

            CategoryResponseDto result = categoryService.getCategoryById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            verify(categoryRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFound when ID inexists")
        void shouldThrowResourceNotFoundWhenIdIsNotExists() {
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.getCategoryById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Category not found");

            verify(categoryRepository, times(1)).findById(99L);
            verifyNoInteractions(categoryMapper);
        }
    }

    @Nested
    @DisplayName("Category exclusion")
    class DeleteCategory {
        @Test
        @DisplayName("Should delete a category by ID successfully")
        void shouldDeleteCategorySuccessfully() {
            when(categoryRepository.existsById(1L)).thenReturn(true);
            doNothing().when(categoryRepository).deleteById(1L);

            assertDoesNotThrow(() -> categoryService.deleteCategoryById(1L));

            verify(categoryRepository, times(1)).existsById(1L);
            verify(categoryRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting inexistent category ID")
        void shouldThrowExceptionWhenDeletingInexistentCategoryId() {
            when(categoryRepository.existsById(99L)).thenReturn(false);
            assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategoryById(99L));

        verify(categoryRepository, times(1)).existsById(99L);
        verify(categoryRepository, never()).deleteById(anyLong());}
    }
}
