package com.budgie.server.service;

import com.budgie.server.dto.CategorySummaryDto;
import com.budgie.server.dto.TransactionDto;
import com.budgie.server.entity.CategoryEntity;
import com.budgie.server.entity.TransactionEntity;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.mapper.TransactionMapper;
import com.budgie.server.repository.CategoryRepository;
import com.budgie.server.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    //내역 가져오기
    public List<TransactionDto> getTransactions(UserEntity user){
        return transactionRepository.findByUser(user)
                .stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
    }

    //월별 내역 가져오기
    public List<TransactionDto> getTransactionsByMonth(UserEntity user, int year, int month){
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return transactionRepository.findByUserAndTransactionDateBetween(user, start, end)
                .stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
    }

    //일 단위 상세 조회
    public List<TransactionDto> getTransactionByDay(UserEntity user, int year, int month, int day){
        LocalDate date = LocalDate.of(year, month, day);
        return transactionRepository.findByUserAndTransactionDateBetween(user, date, date)
                .stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
    }

    //소비, 지출 내역 만들기(생성하기)
    public TransactionDto createTransaction(TransactionEntity transaction){
        TransactionEntity saved = transactionRepository.save(transaction);
        return TransactionMapper.toDto(saved);
    }

    //소비, 지출 내역 수정하기
    public TransactionDto updateTransaction(Long transactionId, TransactionEntity updated){
        TransactionEntity existing = transactionRepository.findById(transactionId)
                .orElseThrow(()->new IllegalArgumentException("내역을 찾을 수 없습니다."));

        //카테고리 조회
        if(updated.getCategory() != null && updated.getCategory().getCategoryId() != null){
            Long categoryId = updated.getCategory().getCategoryId();

            CategoryEntity category = categoryRepository.findById(categoryId)
                    .orElseThrow(()->new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

            existing.setCategory(category);
            existing.setBudgetType(category.getBudgetType());
        }

        existing.setAmount(updated.getAmount());
        existing.setMemo(updated.getMemo());
        existing.setTransactionDate(updated.getTransactionDate());

        return TransactionMapper.toDto(existing);
    }

    //삭제
    public void deletedTransaction(Long transactionId){
        transactionRepository.deleteById(transactionId);
    }

    //월 소비 합계
    public Long getMonthlyExpense(Long userId, Integer year, Integer month){
        return transactionRepository.sumMonthlyExpense(userId, year, month);
    }

    //월 카테고리 합
    public List<CategorySummaryDto> getMonthlyCategorySummary(Long userId, int year, int month){
        return transactionRepository.getMonthlyCategorySummary(userId, year, month);
    }
}
