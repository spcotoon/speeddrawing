package com.spcotoon.speeddrawing.gameStomp.gameRoom.registry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomSession;
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
public class RedisGameRoomSessionRegistry {

    private static final String KEY = "lobby:rooms";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void createRoom(GameRoomSession gameRoomSession) {
        try {
            String json = objectMapper.writeValueAsString(gameRoomSession);
            redisTemplate.opsForHash().put(KEY, gameRoomSession.getRoomId(), json);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRoom(String roomId) {
        redisTemplate.opsForHash().delete(KEY, roomId);
    }

    public List<GameRoomSession> getAllRooms() {
        Map<Object, Object> all = redisTemplate.opsForHash().entries(KEY);
        return all.values().stream()
                .map(o -> {
                    try {
                        return objectMapper.readValue(o.toString(), GameRoomSession.class);
                    } catch (IOException e) {
                        log.error("Failed to deserialize GameRoomSession", e);
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    public GameRoomSession getRoom(String roomId) {
        Object value = redisTemplate.opsForHash().get(KEY, roomId);
        if (value == null) return null;

        try {
            return objectMapper.readValue(value.toString(), GameRoomSession.class);
        } catch (IOException e) {
            log.error("Failed to deserialize GameRoomSession", e);
            throw new RuntimeException(e);
        }
    }
}


