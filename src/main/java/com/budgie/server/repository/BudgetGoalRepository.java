package com.budgie.server.repository;

import com.budgie.server.entity.BudgetGoalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface BudgetGoalRepository extends JpaRepository<BudgetGoalEntity, Long> {

    Optional<BudgetGoalEntity> findByUserIdAndYearAndMonth(Long userId, Integer year, Integer month);
}
