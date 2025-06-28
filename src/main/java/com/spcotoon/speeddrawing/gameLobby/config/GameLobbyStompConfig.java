package com.spcotoon.speeddrawing.gameLobby.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class GameLobbyStompConfig implements WebSocketMessageBrokerConfigurer {
}
