package com.spcotoon.speeddrawing.gameStomp.gameRoomChat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spcotoon.speeddrawing.gameStomp.gameRoomChat.dto.GameRoomChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameRoomChatStompController {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    //publish/game-room/234124/chat
    @MessageMapping("/game-room/{roomId}/chat")
    public void sendMessage(@DestinationVariable Long roomId, GameRoomChatMessageDto chatMessageDto) throws JsonProcessingException {

        String channel = "gameChat:" + roomId;

        String message = objectMapper.writeValueAsString(chatMessageDto);

        redisTemplate.convertAndSend(channel, message);
    }
}
