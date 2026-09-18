package com.portfolio.ecommerceproject.service;

import com.portfolio.ecommerceproject.domain.model.Category;
import com.portfolio.ecommerceproject.domain.repository.CategoryRepository;
import com.portfolio.ecommerceproject.dto.CategoryDto;
import com.portfolio.ecommerceproject.dto.CategoryResponseDto;
import com.portfolio.ecommerceproject.mapper.CategoryMapper;
import com.portfolio.ecommerceproject.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public CategoryResponseDto createCategory(CategoryDto dto) {
        Category category = categoryMapper.toEntity(dto);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDto(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return categoryMapper.toDto(category);
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponseDto> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(c -> categoryMapper.toDto(c));
    }

    @Transactional
    public CategoryResponseDto updateCategoryById(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setName(dto.name());
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDto(savedCategory);
    }

    @Transactional
    public void deleteCategoryById(Long id) {
        if(!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found by id");
        }
        categoryRepository.deleteById(id);
    }
}
