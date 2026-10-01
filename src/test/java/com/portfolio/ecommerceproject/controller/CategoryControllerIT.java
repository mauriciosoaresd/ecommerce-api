package com.portfolio.ecommerceproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.ecommerceproject.domain.model.Category;
import com.portfolio.ecommerceproject.domain.repository.CategoryRepository;
import com.portfolio.ecommerceproject.dto.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class CategoryControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Category category;

    @BeforeEach
    public void setUp() {
        category = new Category();
        category.setName("Eletronics Test");
        categoryRepository.save(category);
    }

    @Nested
    @DisplayName("GET /categories - Public category search")
    class GetCategories {
        @Test
        @DisplayName("Should list all categories and return 200 OK, no authentication needed")
        void shouldListAllCategoriesAndReturn200OK() throws Exception {
            mockMvc.perform(get("/categories")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("Should return a category by existing ID and return 200 OK")
        void shouldCategoryByIDAndReturn200OK() throws Exception {
            mockMvc.perform(get("/categories/{id}", category.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(category.getId()))
                    .andExpect(jsonPath("$.name").value("Eletronics Test"));
        }

        @Test
        @DisplayName("Should return 404 not found when searching inexistent ID category")
        void shouldReturn404NotFoundWhenIDInexists() throws Exception {
            mockMvc.perform(get("/categories/{id}", 999999L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /categories - Category creation")
    class CreateCategory {
        @Test
        @DisplayName("Should create a category and return 201 Created when user role ADMIN")
        void shouldCreateCategoryAndReturn201CreatedWhenUserRoleAdmin() throws Exception {
            CategoryDto categoryDto = new CategoryDto("Peripherals");

            mockMvc.perform(post("/categories")
                    .with(user("user").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(categoryDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Peripherals"));
        }

        @Test
        @DisplayName("Should return 403 forbidden when creating category with user role CLIENT")
        void shouldReturn403ForbiddenWhenUserRoleCLIENT() throws Exception {
            CategoryDto categoryDto = new CategoryDto("Peripherals");

            mockMvc.perform(post("/categories")
                            .with(user("user").roles("CLIENT"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDto)))
                    .andExpect(status().isForbidden());
        }
        
        @Test
        @DisplayName("Should return 401 unauthorized when creating with no JWT token")
        void shouldReturn401UnauthorizedWhenNoValidToken() throws Exception {
            CategoryDto categoryDto = new CategoryDto("Peripherals");

            mockMvc.perform(post("/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDto)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("PUT /categories/{id} - Category update")
    class UpdateCategory {
        @Test
        @DisplayName("Should update a category and return 200 OK when user role ADMIN")
        void shouldUpdateCategoryAndReturn200OKWhenUserRoleAdmin() throws Exception {
            CategoryDto categoryDto = new CategoryDto("Mouse pad");

            mockMvc.perform(put("/categories/{id}", category.getId())
                    .with(user("user").roles("ADMIN"))
                            .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(categoryDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Mouse pad"));
        }

        @Test
        @DisplayName("Should return 403 forbidden when updating with user role client")
        void shouldReturn403ForbiddenWhenUserRoleCLIENT() throws Exception {
            CategoryDto categoryDto = new CategoryDto("Mouse pad");
            mockMvc.perform(put("/categories/{id}", category.getId())
                    .with(user("user").roles("CLIENT"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(categoryDto)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("DELETE /categories/{id} - Category exclusion")
    class DeleteCategory {
        @Test
        @DisplayName("Should delete a category by ID and return 204 No content when user role admin")
        void shouldDeleteCategoryAndReturn204NoContentWhenUserRoleAdmin() throws Exception {
            mockMvc.perform(delete("/categories/{id}", category.getId())
                    .with(user("user").roles("ADMIN"))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 403 forbidden when deleting category with user role client")
        void shouldReturn403ForbiddenWhenUserRoleCLIENT() throws Exception {
            mockMvc.perform(delete("/categories/{id}", category.getId())
                    .with(user("user").roles("CLIENT"))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }
    }
}
