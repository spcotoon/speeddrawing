package com.spcotoon.speeddrawing.gameStomp.config;

import com.spcotoon.speeddrawing.common.auth.JwtTokenProvider;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        //토큰 없이도 커넥트는 가능
        if (StompCommand.CONNECT == accessor.getCommand()) {
            return message;
        }

        // /topic/lobby 구독도 토큰 없이 가능. (비회원이 로비 볼 수 있음)
        if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            String destination = accessor.getDestination();

            if (destination != null && destination.startsWith("/topic/lobby")) {
                return message;
            }
        }

        // 로비 외 (게임 방) 구독이면 토큰 검증(회원만 게임방 입장 가능)
        String bearerToken = accessor.getFirstNativeHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("JWT token is missing or invalid format (Bearer required)");
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
