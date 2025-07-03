package com.spcotoon.speeddrawing.gameStomp.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RedisSessionUserRegistry {

    private static final String KEY = "lobby:sessions";

    private final RedisTemplate<String, String> redisTemplate;

    public void registerUser(String sessionId, String nickname) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        ops.put(KEY, sessionId, nickname);
    }

    public void unregisterUser(String sessionId) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        ops.delete(KEY, sessionId);
    }

    public List<String> getAllConnectedUsers() {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        Map<String, String> all = ops.entries(KEY);
        return new ArrayList<>(all.values());
    }
}
