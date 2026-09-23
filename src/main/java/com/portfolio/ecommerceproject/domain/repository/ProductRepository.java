package com.portfolio.ecommerceproject.domain.repository;

import com.portfolio.ecommerceproject.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE " +
            "(CAST(:name AS String) IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:name AS String), '%'))) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<Product> filterSearch(
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}
