package com.budgie.server.controller;

import com.budgie.server.entity.CategoryEntity;
import com.budgie.server.enums.BudgetType;
import com.budgie.server.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
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
    public List<CategoryEntity> getCategories(@RequestParam(required = false) BudgetType type){
        if(type != null){
            return categoryRepository.findByBudgetType(type);
        }
        return categoryRepository.findAll();
    }
}
