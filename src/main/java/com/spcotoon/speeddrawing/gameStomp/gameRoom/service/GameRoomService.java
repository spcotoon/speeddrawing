package com.spcotoon.speeddrawing.gameStomp.gameRoom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spcotoon.speeddrawing.common.auth.JwtTokenProvider;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.LobbyDataService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.RedisLobbyPushService;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomCreateReqDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.registry.RedisGameRoomSessionRegistry;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomSession;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GameRoomService {

    private final RedisGameRoomSessionRegistry registry;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisLobbyPushService pushService;
    private final LobbyDataService lobbyDataService;

    public String create(GameRoomCreateReqDto dto, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String nickname = "";
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            nickname = jwtTokenProvider.getNicknameFromToken(token);
        }

        String roomId = String.valueOf(System.currentTimeMillis());

        GameRoomSession gameRoomSession = GameRoomSession.builder()
                .roomId(roomId)
                .title(dto.getTitle())
                .status(GameRoomStatus.WAITING)
                .participantsCount(0)
                .maxCount(4)
                .build();

        log.info("방 생성 - title={} | createdBy={} | IP: {} | User-Agent: {} | Referer: {}", gameRoomSession.getTitle(), nickname, clientIp, userAgent, referer);
        registry.createRoom(gameRoomSession);
        try {
            pushService.publishRoomList(lobbyDataService.getCurrentRooms());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return gameRoomSession.getRoomId();
    }

}