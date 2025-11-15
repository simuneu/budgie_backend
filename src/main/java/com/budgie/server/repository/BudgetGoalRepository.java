package com.budgie.server.repository;

import com.budgie.server.entity.BudgetGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetGoalRepository extends JpaRepository<BudgetGoalEntity, Long> {

    Optional<BudgetGoalEntity> findByUserIdAndYearAndMonth(Long userId, Integer year, Integer month);
}
