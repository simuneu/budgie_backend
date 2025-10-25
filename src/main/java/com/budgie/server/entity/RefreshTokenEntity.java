package com.budgie.server.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 604800)
public class RefreshTokenEntity {
    @Id
    private Long userId;
    private String token; //r.t
}
