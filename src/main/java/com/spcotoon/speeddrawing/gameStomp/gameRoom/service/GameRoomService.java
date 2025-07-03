package com.spcotoon.speeddrawing.gameStomp.gameRoom.service;

import com.spcotoon.speeddrawing.common.auth.JwtTokenProvider;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.GameLobbyPushService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.LobbyDataService;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.domain.GameRoom;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomCreateReqDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.repository.GameRoomRepository;
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

    private final GameRoomRepository gameRoomRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final GameLobbyPushService pushService;
    private final LobbyDataService lobbyDataService;

    public Long create(GameRoomCreateReqDto dto, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String nickname = "";
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            nickname = jwtTokenProvider.getNicknameFromToken(token);
        }

        GameRoom gameRoom = dto.toEntity();
        gameRoomRepository.save(gameRoom);

        log.info("방 생성 - title={} | createdBy={} | IP: {} | User-Agent: {} | Referer: {}", gameRoom.getTitle(), nickname, clientIp, userAgent, referer);

        pushService.publishRoomListInLobby(lobbyDataService.getCurrentRooms());
        return gameRoom.getId();
    }

}