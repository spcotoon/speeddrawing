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

    public void registerUser(UserSession userSession) {
        try {
            String json = objectMapper.writeValueAsString(userSession);
            redisTemplate.opsForHash().put(KEY, userSession.getSessionId(), json);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void unregisterUser(String sessionId) {
        Map<Object, Object> all = redisTemplate.opsForHash().entries(KEY);
        for (Map.Entry<Object, Object> entry : all.entrySet()) {
            String jsonOrNickname = entry.getValue().toString();

            try {
                UserSession userSession = objectMapper.readValue(jsonOrNickname, UserSession.class);
                if (sessionId.equals(userSession.getSessionId())) {
                    redisTemplate.opsForHash().delete(KEY, entry.getKey());
                    break;
                }
            } catch (IOException e) {
                // 만약 value가 JSON이 아니면, 그냥 닉네임 문자열일 수 있다
                if (jsonOrNickname.equals(sessionId)) {
                    redisTemplate.opsForHash().delete(KEY, entry.getKey());
                    break;
                }
                log.warn("Failed to parse redis value for key={}, value={}", entry.getKey(), jsonOrNickname, e);
            }
        }
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

    public UserSession getUserBySessionId(String sessionId) {
        Map<Object, Object> all = redisTemplate.opsForHash().entries(KEY);
        for (Object value : all.values()) {
            try {
                UserSession user = objectMapper.readValue(value.toString(), UserSession.class);
                if (sessionId.equals(user.getSessionId())) {
                    return user;
                }
            } catch (IOException e) {
                log.warn("Failed to parse user session JSON", e);
            }
        }
        return null;
    }
}
