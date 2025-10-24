package com.budgie.server.service;

import com.budgie.server.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProvider jwtProvider;

    private final String KEY_PREFIX = "refreshToken";

    //리프레시 토큰 redis에 저장
    public void saveRefreshToken(Long userId, String refreshToken){
        String key = KEY_PREFIX + userId;
        long expirationTime = jwtProvider.getRefreshTokenExpirationTime();

        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                expirationTime,
                TimeUnit.MILLISECONDS
        );
        log.info("refresh토큰을 redis에 저장:{}", userId);
    }

    //userId에 저장된 refresh token조회
    public String getRefreshToken(Long userId){
        String key = KEY_PREFIX+userId;
        return redisTemplate.opsForValue().get(key);
    }

    //리프레시 토큰을 삭제->무효화
    public boolean deletedRefreshToken(Long userId){
        String key = KEY_PREFIX + userId;
        Boolean deleted = redisTemplate.delete(key);
        if(Boolean.TRUE.equals(deleted)){
            log.info("리프레시 토큰을 성공적으로 삭제:{}", userId);
            return true;
        }
        log.warn("refresh token을 삭제하는데 실패:{}", userId);
        return false;
    }

}
