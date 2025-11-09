package com.budgie.server.repository;

import com.budgie.server.entity.TransactionEntity;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.enums.BudgetType;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
