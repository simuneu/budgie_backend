package com.budgie.server.mapper;

import com.budgie.server.dto.TransactionDto;
import com.budgie.server.entity.TransactionEntity;

public class TransactionMapper {
    public static TransactionDto toDto(TransactionEntity entity){
        return TransactionDto.builder()
                .transactionId(entity.getTransactionId())
                .categoryId(entity.getCategory().getCategoryId())
                .categoryName(entity.getCategory().getName().getLabel())
                .budgetType(entity.getBudgetType())
                .amount(entity.getAmount())
                .memo(entity.getMemo())
                .transactionDate(entity.getTransactionDate())
                .build();
    }

    public static TransactionEntity toEntity(TransactionDto dto){
        return TransactionEntity.builder()
                .transactionId(dto.getTransactionId())
                .budgetType(dto.getBudgetType())
                .amount(dto.getAmount())
                .transactionDate(dto.getTransactionDate())
                .build();
    }
}
