package com.spcotoon.speeddrawing.gameStomp.gameLobby.eventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spcotoon.speeddrawing.common.auth.JwtTokenProvider;
import com.spcotoon.speeddrawing.common.auth.JwtTokenUserInfo;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.LobbyDataService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.RedisLobbyPushService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.registry.RedisUserSessionRegistry;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.session.UserSession;
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
    private final RedisUserSessionRegistry registry;
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

            registry.registerUser(userSession);

            pushService.publishUserList(lobbyDataService.getCurrentUsers());
        } else if ("game-room".equals(purpose)) {
            //게임룸에 레지스터
        }


    }

    @EventListener
    public void disconnectHandle(SessionDisconnectEvent event) throws JsonProcessingException {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        if (sessionId != null) {
            // Redis에서 세션 정보 찾아오기
            UserSession userSession = registry.getUserBySessionId(sessionId);
            if (userSession != null) {
                String purpose = userSession.getPurpose();
                if ("lobby".equals(purpose)) {
                    registry.unregisterUser(sessionId);
                    log.info("Lobby WebSocket Disconnect - sessionId: {}", sessionId);
                    pushService.publishUserList(lobbyDataService.getCurrentUsers());
                } else if ("game-room".equals(purpose)) {
                    // 게임룸 레지스트리에서 해당 세션 제거
                }
            } else {
                log.warn("Disconnect: user session not found for sessionId={}", sessionId);
            }
        }
    }
}
