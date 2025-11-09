package com.budgie.server.repository;

import com.budgie.server.entity.CategoryEntity;
import com.budgie.server.enums.BudgetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findByBudgetType(BudgetType type);
}
