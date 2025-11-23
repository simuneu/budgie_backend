package com.budgie.server.repository;

import com.budgie.server.entity.CategoryEntity;
import com.budgie.server.enums.BudgetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findByBudgetType(BudgetType type);
}
