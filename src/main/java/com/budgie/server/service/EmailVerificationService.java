package com.budgie.server.service;

import com.budgie.server.entity.EmailVerificationEntity;
import com.budgie.server.enums.IsVerified;
import com.budgie.server.repository.AuthRepository;
import com.budgie.server.repository.EmailVerificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final EmailVerificationRepository emailVerificationRepository;
    private final AuthRepository authRepository;

    //인증코드 저장
    @Transactional
    public void saveVerificationCode(String email, String code){
        Instant expirtTime = Instant.now().plus(5, ChronoUnit.MINUTES);//유효시간 5분

        //인증 기록 조회
        EmailVerificationEntity verificationEntity = emailVerificationRepository.findByEmail(email)
                .orElseGet(EmailVerificationEntity::new);

        verificationEntity.setEmail(email);
        verificationEntity.setValidCode(code);
        verificationEntity.setExpirationTime(expirtTime);
        verificationEntity.setIsVerified(IsVerified.N);

        emailVerificationRepository.save(verificationEntity);
    }

    //인증코드 검증
    @Transactional
    public void verifyCode(String email, String inputCode){
        //디비 조회
        EmailVerificationEntity verificationEntity = emailVerificationRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("인증 정보가 없습니다."));
        //만료시간 검증
        if(Instant.now().isAfter(verificationEntity.getExpirationTime())){
            throw new RuntimeException("인증코드가 만료되었습니다.");
        }
        //코드일치여부
        if(!verificationEntity.getValidCode().equals(inputCode)){
            throw new RuntimeException("인증코드가 일치하지 않습니다.");
        }
        //검증성공시 인증 값 없데이트
        verificationEntity.setIsVerified(IsVerified.Y);
    }

    public boolean isEmailVerified(String email){
        return emailVerificationRepository.findByEmail(email)
                .map(entity->entity.getIsVerified() == IsVerified.Y)
                .orElse(false);
    }

    @Transactional
    public void clearVerificationStatus(String email){
        emailVerificationRepository.findByEmail(email)
                .ifPresent(entity->{
                    entity.setIsVerified(IsVerified.N);
                });
    }
}
