package com.spcotoon.speeddrawing.gameStomp.gameLobby.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.registry.RedisUserSessionRegistry;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.LobbyDataService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.RedisLobbyPushService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.session.UserSession;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomOutReqDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameSessionPubDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.registry.RedisGameRoomSessionRegistry;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.service.GameSessionDataService;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.service.GameSessionPushService;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LobbyStompController {

    private final LobbyDataService dataService;
    private final RedisLobbyPushService pushService;
    private final RedisGameRoomSessionRegistry roomSessionRegistry;
    private final RedisUserSessionRegistry userSessionRegistry;

    private final GameSessionPushService gameSessionPushService;
    private final GameSessionDataService gameSessionDataService;

    @MessageMapping("/lobby/init")
    public void initLobby() throws JsonProcessingException {
        pushService.publishRoomList(dataService.getCurrentRooms());
        pushService.publishUserList(dataService.getCurrentUsers());
    }

    @MessageMapping("/game-room/{roomId}/info")
    public void initRoom(@DestinationVariable String roomId) throws JsonProcessingException {
        gameSessionPushService.publishGameSession(gameSessionDataService.getCurrentGameSession(roomId));
    }

    @MessageMapping("/room/out")
    public void roomOut(@Payload GameRoomOutReqDto reqDto,
                        @Header("Authorization") String authHeader) throws JsonProcessingException {

        String nickname = reqDto.getNickname();
        String roomId = reqDto.getRoomId();

        if (nickname == null || roomId == null) {
            log.warn("roomOut called with null nickname or roomId");
            return;
        }

        // nickname 기준으로 UserSession 찾기 (추가 메서드 필요)
        UserSession userSession = userSessionRegistry.getUserByNickname(nickname);

        if (userSession != null && roomId.equals(userSession.getRoomId())) {
            roomSessionRegistry.leaveRoom(roomId, nickname);
            userSessionRegistry.unregisterUserByKey(userSession.getRedisKey());  // 예: "member:xxx" 또는 "guest:yyy"
            GameRoomSession room = roomSessionRegistry.getRoom(roomId);

            if (room == null) return;

            GameSessionPubDto gameSessionPubDto = new GameSessionPubDto();
            GameSessionPubDto dto = gameSessionPubDto.from(room);
            log.info("유저 {} 방에서 나감. roomId={}", nickname, roomId);
            gameSessionPushService.publishGameSession(dto);
            pushService.publishRoomList(dataService.getCurrentRooms());
        } else {
            log.warn("roomOut 요청했지만 해당 유저 정보를 찾을 수 없음. nickname={}", nickname);
        }
    }
}