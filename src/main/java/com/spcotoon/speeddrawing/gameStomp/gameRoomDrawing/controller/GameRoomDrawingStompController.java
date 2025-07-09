package com.spcotoon.speeddrawing.gameStomp.gameRoomDrawing.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spcotoon.speeddrawing.gameStomp.gameRoomChat.dto.GameRoomChatMessageDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoomDrawing.dto.GameRoomDrawingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameRoomDrawingStompController {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @MessageMapping("/game-room/{roomId}/drawing")
    public void sendMessage(@DestinationVariable Long roomId, GameRoomDrawingDto drawingDto) throws JsonProcessingException {

        String channel = "gameDrawing:" + roomId;

        String message = objectMapper.writeValueAsString(drawingDto);

        redisTemplate.convertAndSend(channel, message);
    }
}
