package com.aejimenezdev.misfinanzas.category.infrastructure.controller;

import com.aejimenezdev.misfinanzas.auth.domain.ports.TokenProvider;
import com.aejimenezdev.misfinanzas.category.application.usecases.ListCategoriesUseCase;
import com.aejimenezdev.misfinanzas.category.domain.model.Category;
import com.aejimenezdev.misfinanzas.category.domain.model.CategoryType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListCategoriesUseCase listCategoriesUseCase;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Test
    void listReturnsAllCategories() throws Exception {
        when(listCategoriesUseCase.execute(null))
                .thenReturn(List.of(new Category(1L, "Salario", CategoryType.INCOME),
                        new Category(2L, "Alimentación", CategoryType.EXPENSE)));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Salario"))
                .andExpect(jsonPath("$[0].type").value("INCOME"))
                .andExpect(jsonPath("$[1].name").value("Alimentación"));
    }

    @Test
    void listReturnsCategoriesByType() throws Exception {
        when(listCategoriesUseCase.execute(CategoryType.EXPENSE))
                .thenReturn(List.of(new Category(2L, "Alimentación", CategoryType.EXPENSE)));

        mockMvc.perform(get("/api/categories").param("type", "EXPENSE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alimentación"))
                .andExpect(jsonPath("$[0].type").value("EXPENSE"));
    }
}