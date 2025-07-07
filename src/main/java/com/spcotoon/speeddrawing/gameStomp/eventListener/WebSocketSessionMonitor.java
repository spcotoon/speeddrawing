package com.spcotoon.speeddrawing.gameStomp.eventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketSessionMonitor {

    private final SimpUserRegistry simpUserRegistry;
    private final WebSocketMessageBrokerStats brokerStats;

    public void printCurrentSessions() {
        log.info("===== WebSocket Broker Stats =====");
        log.info("{}", brokerStats.getWebSocketSessionStatsInfo());
        log.info("===== SimpUserRegistry Sessions =====");

        simpUserRegistry.getUsers().forEach(user -> {
            log.info("User: {}", user.getName());
            user.getSessions().forEach(session -> {
                log.info(" - SessionId: {}", session.getId());
            });
        });
        log.info("====================================");
    }
}
