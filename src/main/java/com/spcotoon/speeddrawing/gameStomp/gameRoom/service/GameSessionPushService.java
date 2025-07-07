package com.spcotoon.speeddrawing.gameStomp.gameRoom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameSessionPubDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameSessionPushService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publishGameSession(GameSessionPubDto dto) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(dto);
        redisTemplate.convertAndSend("gameRoomInfo:" + dto.getRoomId(), json);
    }
}
