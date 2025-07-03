package com.spcotoon.speeddrawing.gameStomp.gameLobby.eventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spcotoon.speeddrawing.common.auth.JwtTokenProvider;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.LobbyDataService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.RedisLobbyPushService;
import com.spcotoon.speeddrawing.gameStomp.util.RedisSessionUserRegistry;
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
    private final RedisSessionUserRegistry registry;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisLobbyPushService pushService;
    private final LobbyDataService lobbyDataService;
    @EventListener
    public void connectHandle(SessionConnectEvent event) throws JsonProcessingException {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String nickname;
        String authHeader = accessor.getFirstNativeHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            nickname = jwtTokenProvider.getNicknameFromToken(token);
        } else {
            nickname = "Guest#" + UUID.randomUUID().toString().substring(0, 5);
        }

        log.info("WebSocket Connect - sessionId: {}, nickname: {}", accessor.getSessionId(), nickname);

        registry.registerUser(accessor.getSessionId(), nickname);
        pushService.publishUserList(lobbyDataService.getCurrentUsers());
    }

    @EventListener
    public void disconnectHandle(SessionDisconnectEvent event) throws JsonProcessingException {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        if (sessionId != null) {
            registry.unregisterUser(sessionId);
            log.info("WebSocket Disconnect - sessionId: {}", sessionId);
            pushService.publishUserList(lobbyDataService.getCurrentUsers());
        }
    }
}
