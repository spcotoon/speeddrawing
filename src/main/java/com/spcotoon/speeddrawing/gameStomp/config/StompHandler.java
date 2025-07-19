package com.spcotoon.speeddrawing.gameStomp.config;

import com.spcotoon.speeddrawing.common.auth.JwtTokenProvider;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.registry.RedisGameRoomSessionRegistry;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisGameRoomSessionRegistry gameRoomSessionRegistry;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            return message;
        }


        if (StompCommand.SEND == accessor.getCommand()) {
            String destination = accessor.getDestination();
            if (destination != null && destination.startsWith("/publish/lobby")) {
                return message;
            }

            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                throw new MessagingException("JWT token is missing or invalid format (Bearer required) for SEND");
            }

            String token = bearerToken.substring(7);
            Jwts.parserBuilder()
                    .setSigningKey(jwtTokenProvider.getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return message;
        }

        if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            String destination = accessor.getDestination();

            if (destination != null && destination.startsWith("/topic/lobby")) {
                return message;
            } else {
                String bearerToken = accessor.getFirstNativeHeader("Authorization");
                if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                    throw new MessagingException("JWT token is missing or invalid format (Bearer required)");
                }

                String token = bearerToken.substring(7);
                Jwts.parserBuilder()
                        .setSigningKey(jwtTokenProvider.getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                return message;
            }
        }

        return message;
    }
}
