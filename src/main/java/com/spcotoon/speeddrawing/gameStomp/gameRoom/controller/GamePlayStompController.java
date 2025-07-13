package com.spcotoon.speeddrawing.gameStomp.gameRoom.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.service.GameRoomService;
import com.spcotoon.speeddrawing.gameStomp.gameRoomChat.dto.GameChatType;
import com.spcotoon.speeddrawing.gameStomp.gameRoomChat.dto.GameRoomChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GamePlayStompController {

    private final GameRoomService gameRoomService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @MessageMapping("/game-room/{roomId}/start")
    public void initRoom(@DestinationVariable String roomId) throws JsonProcessingException {
        GameRoomChatMessageDto dto = new GameRoomChatMessageDto();
        dto.setType(GameChatType.GAME_START);

        String channel = "gameChat:" + roomId;
        redisTemplate.convertAndSend(
                channel,
                objectMapper.writeValueAsString(dto)
        );

        gameRoomService.startGame(roomId);
    }


}
