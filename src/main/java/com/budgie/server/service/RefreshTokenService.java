package com.budgie.server.service;

import com.budgie.server.entity.RefreshTokenEntity;
import com.budgie.server.entity.UserEntity;
import com.budgie.server.repository.RefreshTokenRepository;
import com.budgie.server.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    //refresh token 저장, 업데이트
    public void saveRefreshToken(Long userId, String refreshToken){
        Long ttlSeconds = jwtProvider.getRefreshTokenExpirationSeconds();

        if(ttlSeconds == null || ttlSeconds <= 0){
            log.warn("Refresh Token TTL이 유효하지 않거나 0입니다. userId: {}", userId);
            return;
        }

        RefreshTokenEntity tokenEntity = RefreshTokenEntity.builder()
                .userId(userId)
                .token(refreshToken)
                .expiration(ttlSeconds)
                .build();

        refreshTokenRepository.save(tokenEntity);
    }

    //삭제
    public boolean deletedRefreshToken(Long userId) {
        if (refreshTokenRepository.existsById(userId)) {
            refreshTokenRepository.deleteById(userId);
            log.info("로그아웃 성공, refresh token 삭제 완료. userId:{}", userId);
            return true;
        } else {
            log.warn("로그아웃 실패: refresh token이 존재하지 않거나 삭제 오류:{}", userId);
            return false;
        }
    }

    //검증 메서드
    public boolean validateRefreshToken(Long userId, String refreshToken) {
        Optional<RefreshTokenEntity> tokenOptional = refreshTokenRepository.findById(userId);

        if (tokenOptional.isPresent()) {
            return tokenOptional.get().getToken().equals(refreshToken);
        }
        return false;
    }


    //refresh토큰을 redis에 저장, 업데이트
    public void saveOrUpdate(UserEntity user, String refreshToken, Instant expiryDate){
        Long ttlSeconds = expiryDate.getEpochSecond() - Instant.now().getEpochSecond();

        if(ttlSeconds <= 0){
            log.warn("발급된 토큰의 만료 시간이 이미 지났습니다. : {}", user.getEmail());
            return;
        }

        RefreshTokenEntity tokenEntity = RefreshTokenEntity.builder()
                .userId(user.getUserId())
                .token(refreshToken)
                .expiration(ttlSeconds)
                .build();

        refreshTokenRepository.save(tokenEntity);
    }

    //userId를 기반으로 redis의 토큰 조회
    public Optional<RefreshTokenEntity> findByUserId(Long userId) {
        return refreshTokenRepository.findById(userId);
    }

     //refresh token문자열 기반으로 조회
    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void deleteByUserId(Long userId) {
        if (refreshTokenRepository.existsById(userId)) {
            refreshTokenRepository.deleteById(userId);
            log.info("리프레시 토큰을 성공적으로 삭제: {}", userId);
        } else {
            log.warn("refresh token을 삭제하는데 실패: {}", userId);
        }
    }
}
