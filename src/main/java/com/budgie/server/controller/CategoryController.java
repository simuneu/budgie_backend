package com.budgie.server.controller;

import com.budgie.server.dto.ApiResponse;
import com.budgie.server.entity.CategoryEntity;
import com.budgie.server.enums.BudgetType;
import com.budgie.server.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryEntity>>> getCategories(@RequestParam(required = false) BudgetType type){
        List<CategoryEntity> categories;

        if(type != null){
            categories = categoryRepository.findByBudgetType(type);
        }else {
            categories = categoryRepository.findAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }
}
