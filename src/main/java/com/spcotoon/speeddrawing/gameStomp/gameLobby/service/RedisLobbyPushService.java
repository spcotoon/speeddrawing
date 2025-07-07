package com.spcotoon.speeddrawing.gameStomp.gameLobby.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.dto.LobbyGameRoomPubDto;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.dto.LobbyUserListPubDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisLobbyPushService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publishRoomList(List<LobbyGameRoomPubDto> roomList) throws JsonProcessingException {
        String rooms = objectMapper.writeValueAsString(roomList);

        redisTemplate.convertAndSend("lobbyRooms", rooms);
    }

    public void publishUserList(LobbyUserListPubDto userList) throws JsonProcessingException {
        String users = objectMapper.writeValueAsString(userList);

        redisTemplate.convertAndSend("lobbyUsers", users);
    }

}
