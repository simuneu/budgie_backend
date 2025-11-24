package com.budgie.server.service;

import com.budgie.server.dto.AuthResponseDto;
import com.budgie.server.dto.LoginRequestDto;
import com.budgie.server.dto.UserSignupRequestDto;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.enums.UserStatus;
import com.budgie.server.repository.AuthRepository;
import com.budgie.server.security.JwtProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import javax.management.relation.RelationNotFoundException;
import javax.naming.AuthenticationException;
import javax.swing.text.html.Option;
import java.security.Principal;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthService(PasswordEncoder passwordEncoder, AuthRepository authRepository,
                       EmailService emailService, EmailVerificationService emailVerificationService,
                       JwtProvider jwtProvider, RefreshTokenService refreshTokenService){
        this.passwordEncoder = passwordEncoder;
        this.authRepository = authRepository;
        this.emailService = emailService;
        this.emailVerificationService = emailVerificationService;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
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
        Optional<UserEntity> existingUserOptional = authRepository.findByEmailWithDeleted(email);
        if(existingUserOptional.isPresent()){
            UserEntity existingUser = existingUserOptional.get();

            //계정 활성화
            if(existingUser.getUserStatus() == UserStatus.Y && existingUser.getDeletedAt() == null){
                log.warn("이미 사용 중인 이메일 : {}", email);
                throw new RuntimeException("이미 사용 중인 이메일");
            }else{
                existingUser.setDeletedAt(null);
                existingUser.setUserStatus(UserStatus.Y);
                existingUser.setNickname(nickname);
                existingUser.setPassword(passwordEncoder.encode(password));

                return authRepository.save(existingUser);
            }
        }

        UserEntity newUser = UserEntity.builder()
                .email(email)
                .nickname(nickname)
                .userStatus(UserStatus.Y)
                .password(passwordEncoder.encode(password))
                .build();
        return authRepository.save(newUser);
    }


    public UserEntity getById(final Long id){
        Optional<UserEntity> userOptional = authRepository.findById(id);
        return userOptional.orElse(null);
    }

    public Optional<UserEntity> findByEmail(String email){
        return authRepository.findByEmail(email);
    }

    //로그인 검증
    @Transactional
    public AuthResponseDto login(LoginRequestDto requestDto){
        final String email = requestDto.getEmail();
        final String password = requestDto.getPassword();

        //이메일로 사용자 조회
        UserEntity loginUser= authRepository.findByEmail(email)
            .orElseThrow(()->{
                log.warn("인증 실패: 존재하지 않는 이메일{}", email);
                throw  new RuntimeException("계정이 존재하지 않습니다.");
            });
        //탈퇴 계정 여부 확인
        if(loginUser.getDeletedAt() !=null || loginUser.getUserStatus() == UserStatus.N){
            log.warn("인증실패:탈퇴 계정 접근{}", email);
            throw new EntityNotFoundException("탈퇴계정으로 재가입이 필요합니다.");
        }

        //비번
        if(!passwordEncoder.matches(password, loginUser.getPassword())){
            log.warn("인증 실패: 비밀번호 불일치 {}", email);
            throw new RuntimeException("비밀번호를 확인해주세요.");
        }

        //로그인 시 토큰 생성
        String accessToken = jwtProvider.createAccessToken(loginUser.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(loginUser.getUserId());

        refreshTokenService.saveRefreshToken(loginUser.getUserId(), refreshToken);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(jwtProvider.getGrantType())
                .build();
    }

    //유저 조회, 생성
    @Transactional
    public UserEntity findOrCreateUser(String email, String nickname){
        return authRepository.findByEmail(email).orElseGet(()->{
            UserEntity newUser = UserEntity.builder()
                    .email(email)
                    .nickname(nickname)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))//더미 비번
                    .build();
            return authRepository.save(newUser);
        });
    }

    //토큰 무효화
    @Transactional
    public void logout(Long userId){
        if(refreshTokenService.deletedRefreshToken(userId)){
            log.info("로그아웃 성공, refresh token 삭제 완료. userId:{}", userId);
        }else{
            log.warn("로그아웃 실패:refresh token이 존재하지 않거나 삭제 오류:{}", userId);
        }
    }

    //RTR
    @Transactional
    public AuthResponseDto refreshAccessToken(String refreshToken){
        //jwt자체 유효성 검사
        if(jwtProvider.validateToken(refreshToken)== false){
            throw new RuntimeException("유효하지 않거나 만료된 token");
        }
        //토큰에서 userId추출
        Long userId = jwtProvider.getUserIdFromToken(refreshToken);

        //redis token과 일치하는지 검즏(rtr보안)
        if(!refreshTokenService.validateRefreshToken(userId, refreshToken)){
            throw new RuntimeException("유효하지 않은 refresh token");
        }

        //검증 성공 시
        //기존 토큰 삭제
        refreshTokenService.deletedRefreshToken(userId);
        //새 토큰 발급
        String newAccessToken = jwtProvider.createAccessToken(userId);
        String newRefreshToken = jwtProvider.createRefreshToken(userId);
        //새 토큰 redis에 저장
        refreshTokenService.saveRefreshToken(userId, newRefreshToken);
        //새 토큰 반환
        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .grantType(jwtProvider.getGrantType())
                .build();
    }

    //비밀번호 재설정
    @Transactional
    public void setPasswordResetCode(String email){
        UserEntity user = authRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("가입되지 않은 이메일입니다."));

        String resetCode = emailService.createVerificationCode();

        emailVerificationService.saveVerificationCode(email, resetCode);

        emailService.sendPasswordResetEmail(email, resetCode);
        log.info("비밀번호 재설정 코드 발송 완료: {}", email);
    }
}
