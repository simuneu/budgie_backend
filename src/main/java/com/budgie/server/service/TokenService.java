package com.budgie.server.service;

import com.budgie.server.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProvider jwtProvider;

    private static final String PREFIX = "redis";

    //refresh token 저장
    public void saveRefreshToken(Long userId, String refreshToken){
        long expireTime = jwtProvider.getAccessTokenExpirationTime();
        redisTemplate.opsForValue().set(
            PREFIX + userId,
            refreshToken,
            expireTime,
            TimeUnit.MILLISECONDS
        );
        log.debug("redis에 refresh token 저장 완료 userId={},TTL={}ms", userId, expireTime );
    }

    //refresh token 조회
    public String getRefreshToken(Long userId){
        Object token = redisTemplate.opsForValue().get(PREFIX+userId);
        return token != null ? token.toString():null;
    }

    //refresh token삭제
    public void deleteRefreshToken(Long userId){
        redisTemplate.delete(PREFIX+userId);
        log.debug("redis에서 refreshToken삭제, userId={}", userId);
    }

    //refresh token일치 여부 확인
    public boolean validateRefreshToken(Long userId, String providedToken){
        String saved = getRefreshToken(userId);
        return saved != null && saved.equals(providedToken);
    }

}
