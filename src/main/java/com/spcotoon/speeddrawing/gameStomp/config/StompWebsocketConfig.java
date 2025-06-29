package com.spcotoon.speeddrawing.gameStomp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class GameLobbyStompConfig implements WebSocketMessageBrokerConfigurer {

    private final String clientUrl;

    public GameLobbyStompConfig(@Value("${client.url}") String clientUrl) {
        this.clientUrl = clientUrl;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .setAllowedOrigins(clientUrl)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/publish");
        registry.enableSimpleBroker("/topic");

    }
}
