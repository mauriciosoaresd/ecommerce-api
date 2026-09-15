package com.portfolio.ecommerceproject.mapper;

import com.portfolio.ecommerceproject.domain.model.Category;
import com.portfolio.ecommerceproject.dto.CategoryDto;
import com.portfolio.ecommerceproject.dto.CategoryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponseDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryDto dto);
}
