package com.spcotoon.speeddrawing.common.config;

import com.spcotoon.speeddrawing.gameStomp.service.RedisGameSubService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    //연결 기본 객체
    @Bean
    @Qualifier("gamePubSub")
    public RedisConnectionFactory gamePubSubFactory() {

        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

        configuration.setHostName(host);
        configuration.setPort(port);

        return new LettuceConnectionFactory(configuration);
    }

    //publish 객체
    @Bean
    @Qualifier("gamePubSub")
    public StringRedisTemplate stringRedisTemplate(@Qualifier("gamePubSub") RedisConnectionFactory redisConnectionFactory) {

        return new StringRedisTemplate(redisConnectionFactory);
    }

    //subscribe 객체
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            @Qualifier("gamePubSub") RedisConnectionFactory redisConnectionFactory,
            MessageListenerAdapter messageListenerAdapter
            ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListenerAdapter, new PatternTopic("lobbyRooms"));
        container.addMessageListener(messageListenerAdapter, new PatternTopic("lobbyUsers"));
        container.addMessageListener(messageListenerAdapter, new PatternTopic("gameChat:*"));
        container.addMessageListener(messageListenerAdapter, new PatternTopic("gameRoomInfo:*"));
        container.addMessageListener(messageListenerAdapter, new PatternTopic("gameDrawing:*"));
        return container;
    }

    //redis에서 수신된 메세지 처리하는 객체
    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisGameSubService redisGameSubService) {

        //GameChatRedisPubSubService 의 특정 메서드가 수신된 메세지 처리하도록 지정

        return new MessageListenerAdapter(redisGameSubService, "onMessage");
    }

}
