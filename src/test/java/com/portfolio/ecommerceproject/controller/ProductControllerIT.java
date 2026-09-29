package com.portfolio.ecommerceproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.ecommerceproject.domain.model.Category;
import com.portfolio.ecommerceproject.domain.model.Product;
import com.portfolio.ecommerceproject.domain.repository.CategoryRepository;
import com.portfolio.ecommerceproject.domain.repository.ProductRepository;
import com.portfolio.ecommerceproject.dto.ProductDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ProductControllerIT {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        category = new Category(null, "Eletronics");
        categoryRepository.save(category);

        product = new Product();
        product.setName("Notebook");
        product.setDescription("New gen notebook");
        product.setPrice(new BigDecimal("4500.00"));
        product.setQuantity(5);
        product.setCategory(category);
        product = productRepository.save(product);
    }

    @Nested
    @DisplayName("GET /products - Products search(public)")
    class GetProducts {
        @Test
        @DisplayName("Should get a paginated product list and return status 200 OK")
        void shouldGetProductsPageAndReturnStatus200OK() throws Exception {
            mockMvc.perform(get("/products")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[*].name", hasItem("Notebook")))
                    .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(1)));
        }

        @Test
        @DisplayName("Should search products by ID and return status 200 OK")
        void shouldSearchProductsByIdAndReturnStatus200OK() throws Exception {
            mockMvc.perform(get("/products/{id}", product.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(product.getId()))
                    .andExpect(jsonPath("$.name").value("Notebook"));
        }

        @Test
        @DisplayName("Should return 404 Not Found when searching inexistent ID")
        void shouldReturn404NotFoundWhenSearchingInexistentID() throws Exception {
            mockMvc.perform(get("/products/{id}", 99L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /products - Product creation (security)")
    class PostProducts {
        @Test
        @DisplayName("Should return 401 Unauthorized when creating product unauthenticated")
        void shouldReturn401UnauthorizedWhenCreatingProductUnauthenticated() throws Exception {
            ProductDto productDto = new ProductDto("Mouse", "Gamer mouse", new BigDecimal("90.00"), 10, category.getId());

            mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productDto)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return 403 Forbidden when user ROLE_CLIENT tries to create product")
        void shouldReturn403ForbiddenWhenCreatingProduct() throws Exception {
            ProductDto productDto = new ProductDto("Mouse", "Gamer mouse", new BigDecimal("90.00"), 10, category.getId());

            mockMvc.perform(post("/products")
                            .with(user("user").roles("CLIENT"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productDto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should create a product and return 201 Created when user ROLE_ADMIN")
        void shouldCreateProductAndReturn201CreatedWhenCreatingProduct() throws Exception {
            ProductDto productDto = new ProductDto("Mechanic Keyboard", "Mechanic gamer keyboard RGB", new BigDecimal("350.00"), 8, category.getId());

            mockMvc.perform(post("/products")
                            .with(user("user").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productDto)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.name").value("Mechanic Keyboard"));
        }
    }

    @Nested
    @DisplayName("DELETE /product/{id} - Product Deletion (security)")
    class DeleteProduct {
        @Test
        @DisplayName("Should delete product and return 204 no content when ID exists")
        void shouldDeleteProductAndReturn204NoContentWhenDeletingProduct() throws Exception {
            mockMvc.perform(delete("/products/{id}", product.getId())
                            .with(user("user").roles("ADMIN"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }
}
