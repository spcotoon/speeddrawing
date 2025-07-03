package com.spcotoon.speeddrawing.gameStomp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisGameSubService implements MessageListener {

    private final SimpMessageSendingOperations messageTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String actualChannel = new String(message.getChannel());
        String body = new String(message.getBody());

        // 예시: GameMessageDto 같은 객체로 변환하여 사용
        // GameMessageDto chatMessage = objectMapper.readValue(body, GameMessageDto.class);

        // 패턴 기반으로 동적 라우팅
        if (actualChannel.startsWith("gameChat:")) {
            // "gameChat:123"에서 "123"을 추출
            String roomId = actualChannel.substring("gameChat:".length());
            // 동적 토픽으로 메시지 전송
            messageTemplate.convertAndSend("/topic/game-room/" + roomId + "/chat", body);
            return; // 처리 후 종료
        }

        if (actualChannel.startsWith("gameRoomInfo:")) {
            String roomId = actualChannel.substring("gameRoomInfo:".length());
            messageTemplate.convertAndSend("/topic/game-room/" + roomId + "/info", body);
            return;
        }

        if (actualChannel.startsWith("gameDrawing:")) {
            String roomId = actualChannel.substring("gameDrawing:".length());
            messageTemplate.convertAndSend("/topic/game-room/" + roomId + "/drawing", body);
            return;
        }


        // 기존 고정 채널 처리 [여긴 완료]
        switch (actualChannel) {
            case "lobbyRooms":
                messageTemplate.convertAndSend("/topic/lobby/rooms", body);
                break;
            case "lobbyUsers":
                messageTemplate.convertAndSend("/topic/lobby/users", body);
                break;
        }
    }
}