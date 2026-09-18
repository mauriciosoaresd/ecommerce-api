package com.portfolio.ecommerceproject.domain.repository;

import com.portfolio.ecommerceproject.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
