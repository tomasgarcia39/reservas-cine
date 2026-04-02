package com.tomi.reservas_cine.security;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    public TokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void invalidar(String token, long expiracionEnSegundos) {
        redisTemplate.opsForValue().set(
                "blacklist:" + token,
                "invalidado",
                expiracionEnSegundos,
                TimeUnit.SECONDS
        );
    }

    public boolean estaInvalidado(String token) {
        return redisTemplate.hasKey("blacklist:" + token);
    }
}