package com.budgie.server.controller;

import com.budgie.server.dto.TransactionDto;
import com.budgie.server.entity.CategoryEntity;
import com.budgie.server.entity.TransactionEntity;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.mapper.TransactionMapper;
import com.budgie.server.repository.CategoryRepository;
import com.budgie.server.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;

    //월별. 일별, 전체 조회
    // --http://localhost:8080/api/transactions?year=2025&month=11
    // transactions?year=2025&month=1&day=14
    @GetMapping
    public List<TransactionDto> getTransaction(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day,
            Principal principal){
        UserEntity user = new UserEntity();
        user.setUserId(Long.parseLong(principal.getName()));

        if(year != null && month != null && day != null){
            return transactionService.getTransactionByDay(user, year,month, day);
        } else if (year != null && month != null) {
            return transactionService.getTransactionsByMonth(user, year, month);
        }else {
            return transactionService.getTransactions(user);
        }
    }

    //등록
    @PostMapping
    public TransactionDto createTransaction(@RequestBody TransactionDto dto, Principal principal){
        UserEntity user = new UserEntity();
        user.setUserId(Long.parseLong(principal.getName()));

        TransactionEntity entity = TransactionMapper.toEntity(dto);
        entity.setUser(user);

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));
        entity.setCategory(category);

        return transactionService.createTransaction(entity);
    }

    //수정
    @PutMapping("/{transactionId}")
    public TransactionDto updateTransaction(
            @PathVariable Long transactionId,
            @RequestBody TransactionDto dto
    ){
        TransactionEntity updated = TransactionMapper.toEntity(dto);

        CategoryEntity category = new CategoryEntity();
        category.setCategoryId(dto.getCategoryId());
        updated.setCategory(category);

        return transactionService.updateTransaction(transactionId, updated);
    }

    //삭제
    @DeleteMapping("{transactionId}")
    public void deleteTransaction(@PathVariable Long transactionId){
        transactionService.deletedTransaction(transactionId);
    }

    //월 소비 합계
    @GetMapping("/summary")
    public Map<String, Long> getMonthlySummary(@RequestParam Integer year, Integer month, Principal principal){
        Long userId = Long.parseLong(principal.getName());
        Long totalExpense = transactionService.getMonthlyExpense(userId, year, month);

        return Map.of("totalExpense", totalExpense);
    }
}
