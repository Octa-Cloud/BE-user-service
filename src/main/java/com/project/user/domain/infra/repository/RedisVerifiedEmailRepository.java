package com.project.user.domain.infra.repository;

import com.project.user.domain.domain.repository.VerifiedEmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisVerifiedEmailRepository implements VerifiedEmailRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String VERIFIED_EMAIL = "VERIFIED_EMAIL:";

    @Override
    public void save(String email) {
        redisTemplate.opsForValue().set(VERIFIED_EMAIL + email, "");
    }

    @Override
    public void clearAfterSignUp(String email) {
        redisTemplate.delete(VERIFIED_EMAIL + email);
    }

    @Override
    public boolean isExist(String email) {
        return redisTemplate.hasKey(VERIFIED_EMAIL + email);
    }
}
