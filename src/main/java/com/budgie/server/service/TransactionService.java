package com.budgie.server.service;

import com.budgie.server.dto.TransactionDto;
import com.budgie.server.entity.TransactionEntity;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.mapper.TransactionMapper;
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
    public TransactionDto updateTransaction(Long id, TransactionEntity updated){
        TransactionEntity existing = transactionRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("내역을 찾을 수 없습니다."));
        existing.setCategory(updated.getCategory());
        existing.setAmount(updated.getAmount());
        existing.setBudgetType(updated.getBudgetType());
        existing.setMemo(updated.getMemo());
        existing.setTransactionDate(updated.getTransactionDate());

        return TransactionMapper.toDto(existing);
    }

    //삭제
    public void deletedTransaction(Long id){
        transactionRepository.deleteById(id);
    }
}
