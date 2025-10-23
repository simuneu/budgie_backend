package com.budgie.server.service;

import com.budgie.server.dto.AuthResponseDto;
import com.budgie.server.dto.UserSignupRequestDto;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.enums.UserStatus;
import com.budgie.server.repository.AuthRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;

    public AuthService(PasswordEncoder passwordEncoder, AuthRepository authRepository
                            , EmailService emailService, EmailVerificationService emailVerificationService){
        this.passwordEncoder = passwordEncoder;
        this.authRepository = authRepository;
        this.emailService = emailService;
        this.emailVerificationService = emailVerificationService;
    }
    //패스워드
    private static final String PASSWORD_REGEX =
            "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$";

    //이메일 인증
    @jakarta.transaction.Transactional
    public void sendVerificationEmail(String email){
        //인증코드 생성
        String verificationCode = emailService.createVerificationCode();
        //코드, 만료시간 저장/업데이트
        emailVerificationService.saveVerificationCode(email, verificationCode);
        emailService.sendVerificationEmail(email, verificationCode);
    }

    //이메일 인증 코드 최종 검증 auth_status업데이트
    @jakarta.transaction.Transactional
    public void completeEmailVerification(String email, String inputCode){
        emailVerificationService.verifyCode(email, inputCode);
    }

    //회원가입
    @Transactional
    public UserEntity signup(final UserSignupRequestDto signupRequestDto){
        final String email = signupRequestDto.getEmail();
        final String password = signupRequestDto.getPassword();
        final String nickname = signupRequestDto.getNickname();

        if(!password.matches(PASSWORD_REGEX)){
            log.warn("패스워드 불일치");
            throw new RuntimeException("비밀번호는 영문, 숫자, 특수문자 포함 8자리여야 합니다.");
        }

        //중복 이메일 검사. 이메일 인증
        if(!emailVerificationService.isEmailVerified(email)){
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        //삭제된 계정 포함한 이메일 조회

        UserEntity newUser = UserEntity.builder()
                .email(email)
                .nickname(nickname)
                .userStatus(UserStatus.Y)
                .password(passwordEncoder.encode(password))
                .build();
        return authRepository.save(newUser);
    }

}
