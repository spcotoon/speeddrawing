package com.spcotoon.speeddrawing.gameStomp.gameRoomChat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.registry.RedisGameRoomSessionRegistry;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.service.GameRoomService;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.service.GameSessionDataService;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.service.GameSessionPushService;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomSession;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomStatus;
import com.spcotoon.speeddrawing.gameStomp.gameRoomChat.dto.GameChatType;
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

    private final RedisGameRoomSessionRegistry gameRoomSessionRegistry;
    private final GameSessionPushService gameSessionPushService;
    private final GameSessionDataService gameSessionDataService;
    private final GameRoomService gameRoomService;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final int CORRECT_POINT = 10;

    //publish/game-room/234124/chat
    @MessageMapping("/game-room/{roomId}/chat")
    public void sendMessage(@DestinationVariable Long roomId, GameRoomChatMessageDto chatMessageDto) throws JsonProcessingException {

        String channel = "gameChat:" + roomId;

        GameRoomSession room = gameRoomSessionRegistry.getRoom(String.valueOf(roomId));
        if (room == null) {
            log.warn("방이 존재하지 않습니다. roomId={}", roomId);
            return;
        }

        if (room.getStatus() == GameRoomStatus.PLAYING) {
            String quiz = room.getCurrentQuiz();

            if (chatMessageDto.getContent() != null && chatMessageDto.getContent().equals(quiz)) {
                room.getScore().put(
                        chatMessageDto.getNickname(),
                        room.getScore().getOrDefault(chatMessageDto.getNickname(), 0) + CORRECT_POINT
                );

                gameRoomSessionRegistry.updateRoom(room);

                gameSessionPushService.publishGameSession(gameSessionDataService.getCurrentGameSession(String.valueOf(roomId)));

                gameRoomService.nextTurn(String.valueOf(roomId), chatMessageDto.getNickname());

                return;
            }
        }

        chatMessageDto.setType(GameChatType.CHAT);

        String message = objectMapper.writeValueAsString(chatMessageDto);

        redisTemplate.convertAndSend(channel, message);
    }

    @MessageMapping("/game-room/{roomId}/timeout")
    public void handleTimeout(@DestinationVariable String roomId) throws JsonProcessingException {

        gameRoomService.nextTurn(String.valueOf(roomId), null);
    }

    @MessageMapping("/game-room/{roomId}/finish")
    public void forceFinnish(@DestinationVariable Long roomId) throws JsonProcessingException {
        GameRoomSession session = gameRoomSessionRegistry.getRoom(String.valueOf(roomId));
        gameRoomService.finishGame(session);
    }
}
