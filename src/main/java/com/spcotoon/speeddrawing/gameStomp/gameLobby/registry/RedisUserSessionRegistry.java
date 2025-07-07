package com.spcotoon.speeddrawing.gameStomp.gameLobby.registry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.session.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUserSessionRegistry {

    private static final String KEY = "lobby:users";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private String getRedisKey(UserSession userSession) {
        if (userSession.getMemberId() != null) {
            return "member:" + userSession.getMemberId();
        } else {
            return "guest:" + userSession.getSessionId();
        }
    }

    public void unregisterUserByKey(String key) {
        if (key == null || key.isEmpty()) return;
        redisTemplate.opsForHash().delete(KEY, key);
    }

    public void registerUser(UserSession userSession) {
        try {
            String json = objectMapper.writeValueAsString(userSession);
            String redisKey = getRedisKey(userSession);
            redisTemplate.opsForHash().put(KEY, redisKey, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public UserSession getUserByNickname(String nickname) {
        Map<Object, Object> all = redisTemplate.opsForHash().entries(KEY);
        for (Map.Entry<Object, Object> entry : all.entrySet()) {
            try {
                UserSession user = objectMapper.readValue(entry.getValue().toString(), UserSession.class);
                if (nickname.equals(user.getNickname())) {
                    // UserSession에 redisKey 저장할 수 있도록 setter 또는 빌더에 추가해두면 좋음
                    user.setRedisKey(entry.getKey().toString());
                    return user;
                }
            } catch (IOException e) {
                log.warn("Failed to parse user session JSON", e);
            }
        }
        return null;
    }
    public void unregisterUserByMemberId(Long memberId) {
        if (memberId == null) return;
        String key = "member:" + memberId;
        redisTemplate.opsForHash().delete(KEY, key);
    }

    public void unregisterUserBySessionId(String sessionId) {
        Map<Object, Object> all = redisTemplate.opsForHash().entries(KEY);
        for (Map.Entry<Object, Object> entry : all.entrySet()) {
            try {
                UserSession user = objectMapper.readValue(entry.getValue().toString(), UserSession.class);
                if (sessionId.equals(user.getSessionId())) {
                    redisTemplate.opsForHash().delete(KEY, entry.getKey());
                    return;
                }
            } catch (IOException e) {
                log.warn("Failed to parse user session JSON", e);
            }
        }
    }

    public UserSession getUserBySessionId(String sessionId) {
        Map<Object, Object> all = redisTemplate.opsForHash().entries(KEY);
        for (Map.Entry<Object, Object> entry : all.entrySet()) {
            try {
                UserSession user = objectMapper.readValue(entry.getValue().toString(), UserSession.class);
                if (sessionId.equals(user.getSessionId())) {
                    return user;
                }
            } catch (IOException e) {
                log.warn("Failed to parse user session JSON", e);
            }
        }
        return null;
    }

    public List<UserSession> getAllConnectedUsers() {
        Map<Object, Object> all = redisTemplate.opsForHash().entries(KEY);
        return all.values().stream()
                .map(o -> {
                    try {
                        return objectMapper.readValue(o.toString(), UserSession.class);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }
}
