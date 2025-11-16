package com.budgie.server.repository;

import com.budgie.server.dto.CategorySummaryDto;
import com.budgie.server.dto.RecordedDayDto;
import com.budgie.server.entity.TransactionEntity;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.enums.BudgetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    //유저 별 전체 내역 조회
    List<TransactionEntity> findByUser(UserEntity user);

    //특정 기간(월) 조회
    List<TransactionEntity> findByUserAndTransactionDateBetween(UserEntity user, LocalDate start, LocalDate end );

    //타입 별 (소득/ 지출 조회)
    List<TransactionEntity> findByUserAndBudgetType(UserEntity user, BudgetType type);

    //월 소비 합계
    @Query("SELECT COALESCE(SUM(t.amount), 0) " +
            "FROM TransactionEntity t " +
            "WHERE t.user.userId = :userId " +
            "AND YEAR(t.transactionDate) = :year " +
            "AND MONTH(t.transactionDate) = :month " +
            "AND t.budgetType = 'EXP'")
    Long sumMonthlyExpense(Long userId, Integer year, Integer month);

    //월별 카테고리별 지툴 합
    @Query("""
        SELECT new com.budgie.server.dto.CategorySummaryDto(
            c.name,
            SUM(t.amount)
        )
        FROM TransactionEntity t
        JOIN t.category c
        WHERE t.user.userId = :userId
          AND YEAR(t.transactionDate) = :year
          AND MONTH(t.transactionDate) = :month
          AND t.budgetType = com.budgie.server.enums.BudgetType.EXP
        GROUP BY c.name
        ORDER BY SUM(t.amount) DESC
    """)
    List<CategorySummaryDto> getMonthlyCategorySummary(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );


    @Query("""
        SELECT new com.budgie.server.dto.CategorySummaryDto(
            c.name,
            SUM(t.amount)
        )
        FROM TransactionEntity t
        JOIN t.category c
        WHERE t.user.userId = :userId
          AND YEAR(t.transactionDate) = :year
          AND MONTH(t.transactionDate) = :month
          AND t.budgetType = com.budgie.server.enums.BudgetType.INCOME
        GROUP BY c.name
        ORDER BY SUM(t.amount) DESC """)
    List<CategorySummaryDto> getMonthlyIncomeSummary(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    @Query("""
        SELECT new com.budgie.server.dto.RecordedDayDto(
            EXTRACT(DAY FROM t.transactionDate),
            SUM(t.amount)
        )
        FROM TransactionEntity t
        WHERE EXTRACT(YEAR FROM t.transactionDate) = :year
          AND EXTRACT(MONTH FROM t.transactionDate) = :month
          AND t.user.userId = :userId
        GROUP BY EXTRACT(DAY FROM t.transactionDate)
    """)
    List<RecordedDayDto> findRecordedDays(
            @Param("year") int year,
            @Param("month") int month,
            @Param("userId") Long userId
    );


}
