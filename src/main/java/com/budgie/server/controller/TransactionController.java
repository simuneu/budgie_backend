package com.budgie.server.controller;

import com.budgie.server.dto.ApiResponse;
import com.budgie.server.dto.RecordedDayDto;
import com.budgie.server.dto.TransactionDto;
import com.budgie.server.entity.CategoryEntity;
import com.budgie.server.entity.TransactionEntity;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.mapper.TransactionMapper;
import com.budgie.server.repository.CategoryRepository;
import com.budgie.server.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTransaction(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day,
            Principal principal){

        UserEntity user = new UserEntity();
        user.setUserId(Long.parseLong(principal.getName()));

        List<TransactionDto> result;

        if(year != null && month != null && day != null){
            result = transactionService.getTransactionByDay(user, year, month, day);
        } else if (year != null && month != null) {
            result = transactionService.getTransactionsByMonth(user, year, month);
        }else {
            result = transactionService.getTransactions(user);
        }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    //등록
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionDto>> createTransaction(@RequestBody TransactionDto dto, Principal principal){

        UserEntity user = new UserEntity();
        user.setUserId(Long.parseLong(principal.getName()));

        TransactionEntity entity = TransactionMapper.toEntity(dto);
        entity.setUser(user);

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));

        entity.setCategory(category);
        entity.setBudgetType(category.getBudgetType());

        TransactionDto result = transactionService.createTransaction(entity);

        return ResponseEntity.ok(ApiResponse.ok(result));

    }

    //수정
    @PutMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionDto>> updateTransaction(
            @PathVariable Long transactionId,
            @RequestBody TransactionDto dto
    ){
        TransactionEntity updated = TransactionMapper.toEntity(dto);

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리 id를 찾을 수 없음: " + dto.getCategoryId()));

        updated.setCategory(category);

        TransactionDto result =
                transactionService.updateTransaction(transactionId, updated);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    //삭제
    @DeleteMapping("{transactionId}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long transactionId){
        transactionService.deletedTransaction(transactionId);

        return ResponseEntity.ok(ApiResponse.okMessage("삭제 완료"));
    }

    //월 소비 합계
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getMonthlySummary(@RequestParam Integer year, Integer month, Principal principal){
        Long userId = Long.parseLong(principal.getName());
        Long totalExpense = transactionService.getMonthlyExpense(userId, year, month);

        return ResponseEntity.ok(
                ApiResponse.ok(Map.of("totalExpense", totalExpense))
        );
    }

    //월 카테고리 합계 - 지출
    @GetMapping("/summary/category")
    public ResponseEntity<ApiResponse<?>> getCategorySummary(@RequestParam int year, @RequestParam int month, @AuthenticationPrincipal UserDetails user){

        Long userId = Long.parseLong(user.getUsername());

        return ResponseEntity.ok(
                ApiResponse.ok(transactionService.getMonthlyCategorySummary(userId, year, month))
        );
    }

    //월 카테고리 합계 - 수입
    @GetMapping("/summary/category/income")
    public ResponseEntity<ApiResponse<?>> getIncomeCategorySummary(@RequestParam int year, @RequestParam int month, @AuthenticationPrincipal UserDetails user){
        Long userId = Long.parseLong(user.getUsername());

        return ResponseEntity.ok(
                ApiResponse.ok(transactionService.getMonthlyIncomeSummary(userId, year, month))
        );
    }

    //기록 일 조회
    @GetMapping("/days")
    public ResponseEntity<ApiResponse<List<RecordedDayDto>>> getRecordedDays(@RequestParam int year, @RequestParam int month,
                                                                @AuthenticationPrincipal UserDetails user){
        Long userId = Long.parseLong(user.getUsername());

        List<RecordedDayDto> result = transactionService.getRecordedDays(year, month, userId);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }


}
