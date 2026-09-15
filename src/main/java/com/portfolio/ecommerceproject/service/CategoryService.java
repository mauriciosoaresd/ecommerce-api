package com.portfolio.ecommerceproject.service;

import com.portfolio.ecommerceproject.domain.model.Category;
import com.portfolio.ecommerceproject.domain.repository.CategoryRepository;
import com.portfolio.ecommerceproject.dto.CategoryDto;
import com.portfolio.ecommerceproject.dto.CategoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryResponseDto createCategory(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.name());

        Category savedCategory = categoryRepository.save(category);
        return new CategoryResponseDto(savedCategory.getId(), savedCategory.getName());
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponseDto> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(c -> new CategoryResponseDto(c.getId(), c.getName()));
    }
}
