package com.spcotoon.speeddrawing.gameStomp.gameRoom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spcotoon.speeddrawing.common.auth.JwtTokenProvider;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.registry.RedisUserSessionRegistry;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.LobbyDataService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.RedisLobbyPushService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.session.UserSession;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomCreateReqDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomJoinReqDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.registry.RedisGameRoomSessionRegistry;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomSession;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final RedisGameRoomSessionRegistry gameSessionRegistry;
    private final RedisUserSessionRegistry userRegistry;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisLobbyPushService lobbyPushService;
    private final LobbyDataService lobbyDataService;
    private final GameSessionPushService gameSessionPushService;
    private final GameSessionDataService gameSessionDataService;

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
        gameSessionRegistry.createRoom(gameRoomSession);
        try {
            lobbyPushService.publishRoomList(lobbyDataService.getCurrentRooms());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return gameRoomSession.getRoomId();
    }

    public UserSession joinRoom(GameRoomJoinReqDto gameRoomJoinReqDto, HttpServletRequest request) throws JsonProcessingException {
        String authHeader = request.getHeader("Authorization");
        String nickname = "";
        Long memberId = null;
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            nickname = jwtTokenProvider.getNicknameFromToken(token);
            memberId = jwtTokenProvider.getUserInfoFromToken(token).getMemberId();
        } else {
            nickname = gameRoomJoinReqDto.getNickname();
        }


        UserSession userSession = UserSession.builder()
                .nickname(nickname)
                .memberId(memberId)
                .purpose("game-room")
                .roomId(gameRoomJoinReqDto.getRoomId())
                .build();
        userRegistry.registerUser(userSession);
        gameSessionRegistry.joinRoom(gameRoomJoinReqDto.getRoomId(), nickname);
        gameSessionRegistry.getRoom(gameRoomJoinReqDto.getRoomId());
        lobbyPushService.publishUserList(lobbyDataService.getCurrentUsers());
        lobbyPushService.publishRoomList(lobbyDataService.getCurrentRooms());
        gameSessionPushService.publishGameSession(gameSessionDataService.getCurrentGameSession(gameRoomJoinReqDto.getRoomId()));

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        log.info("[{}] 게임방 참가 | roomId={} | nickname={} | IP={} | UA={} | Referer={}",
                now,
                gameRoomJoinReqDto.getRoomId(),
                nickname,
                clientIp,
                userAgent,
                referer
        );

        return userSession;
    }
}