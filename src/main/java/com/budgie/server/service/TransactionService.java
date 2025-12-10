package com.budgie.server.service;

import com.budgie.server.dto.*;
import com.budgie.server.entity.CategoryEntity;
import com.budgie.server.entity.TransactionEntity;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.enums.AlertType;
import com.budgie.server.mapper.TransactionMapper;
import com.budgie.server.repository.CategoryRepository;
import com.budgie.server.repository.TransactionRepository;
import com.budgie.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    private final AnalysisService analysisService;
    private final AlertService alertService;
    private final FcmService fcmService;
    private final UserRepository userRepository;


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

        //알림 로직
        Long userId = saved.getUser().getUserId();
        int year = saved.getTransactionDate().getYear();
        int month = saved.getTransactionDate().getMonthValue();

        //분석데이터 가져오기
        SpendingPaceResponseDto pace = analysisService.getSpendingPace(userId, year, month);

        long used = pace.getTotalExpense();
        long goal = pace.getBudgetGoal();

        if (goal > 0) {
            int rate = analysisService.calculateUsageRate(used, goal);

            if (rate >= 70 && rate < 80) {
                sendPercentAlert(userId, 70);
            } else if (rate >= 80 && rate < 90) {
                sendPercentAlert(userId, 80);
            } else if (rate >= 90 && rate < 100) {
                sendPercentAlert(userId, 90);
            }
        }

        return TransactionMapper.toDto(saved);
    }

    private void sendPercentAlert(Long userId, int percent){
        String message = "🔥 이번 달 예산의 " + percent + "%를 사용했어요! 지출 조절이 필요해요!!";

        // DB 저장
        alertService.createAlert(userId, AlertType.BUDGET_DANGER, message);

        // FCM 발송
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getFcmToken() != null) {
            fcmService.send(
                    user.getFcmToken(),
                    "예산 " + percent + "% 사용",
                    message
            );
        }
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

        Long userId = existing.getUser().getUserId();
        int year = existing.getTransactionDate().getYear();
        int month = existing.getTransactionDate().getMonthValue();

        SpendingPaceResponseDto pace = analysisService.getSpendingPace(userId, year, month);
        long used = pace.getTotalExpense();
        long goal = pace.getBudgetGoal();

        if (goal > 0) {
            int rate = analysisService.calculateUsageRate(used, goal);

            if (rate >= 70 && rate < 80) {
                sendPercentAlert(userId, 70);
            } else if (rate >= 80 && rate < 90) {
                sendPercentAlert(userId, 80);
            } else if (rate >= 90 && rate < 100) {
                sendPercentAlert(userId, 90);
            }
        }

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

    //월 카테고리 합 - 지출
    public List<CategorySummaryDto> getMonthlyCategorySummary(Long userId, int year, int month){
        return transactionRepository.getMonthlyCategorySummary(userId, year, month);
    }

    //월 카테고리 합 - 수입
    public List<CategorySummaryDto> getMonthlyIncomeSummary(Long userId, int year, int month) {
        return transactionRepository.getMonthlyIncomeSummary(userId, year, month);
    }

    //기록한 낳 조회
    public List<RecordedDayDto> getRecordedDays(int year, int month, Long userId){
        return transactionRepository.findRecordedDays(year, month, userId);
    }

    //

}
