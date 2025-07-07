package com.spcotoon.speeddrawing.gameStomp.eventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spcotoon.speeddrawing.common.auth.JwtTokenProvider;
import com.spcotoon.speeddrawing.common.auth.JwtTokenUserInfo;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.LobbyDataService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.RedisLobbyPushService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.registry.RedisUserSessionRegistry;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.session.UserSession;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.registry.RedisGameRoomSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompEventListener {

    private final RedisUserSessionRegistry userRegistry;
    private final RedisGameRoomSessionRegistry roomRegistry;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisLobbyPushService pushService;
    private final LobbyDataService lobbyDataService;

    @EventListener
    public void connectHandle(SessionConnectEvent event) throws JsonProcessingException {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String purpose = accessor.getFirstNativeHeader("purpose");

        if ("lobby".equals(purpose)) {
            Long memberId = null;
            String nickname;
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                JwtTokenUserInfo userInfoFromToken = jwtTokenProvider.getUserInfoFromToken(token);

                memberId = userInfoFromToken.getMemberId();
                nickname = userInfoFromToken.getNickname();
            } else {
                nickname = "Guest#" + UUID.randomUUID().toString().substring(0, 5);
            }

            log.info("WebSocket Connect - sessionId: {}, nickname: {}", accessor.getSessionId(), nickname);

            UserSession userSession = UserSession.builder()
                    .sessionId(accessor.getSessionId())
                    .nickname(nickname)
                    .memberId(memberId)
                    .purpose(purpose)
                    .build();

            userRegistry.registerUser(userSession);
            pushService.publishUserList(lobbyDataService.getCurrentUsers());
        }
        // game-room 은 여기서 joinRoom 하지 않음 (Interceptor (StompHandler) 에서 이미 처리됨)
    }

    @EventListener
    public void disconnectHandle(SessionDisconnectEvent event) throws JsonProcessingException {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        if (sessionId != null) {
            UserSession userSession = userRegistry.getUserBySessionId(sessionId);
            if (userSession != null) {
                String purpose = userSession.getPurpose();
                if ("lobby".equals(purpose)) {
                    userRegistry.unregisterUserBySessionId(sessionId);
                    log.info("Lobby WebSocket Disconnect - sessionId: {}", sessionId);
                    pushService.publishUserList(lobbyDataService.getCurrentUsers());
                }
                // game-room disconnect는 pub 메시지에서 처리하므로 여기선 안함
            }
        }
    }
}
