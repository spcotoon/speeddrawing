package com.spcotoon.speeddrawing.gameStomp.gameLobby.service;

import com.spcotoon.speeddrawing.gameStomp.gameLobby.dto.LobbyGameRoomPubDto;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.dto.LobbyUserDto;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.dto.LobbyUserListPubDto;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.session.UserSession;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.registry.RedisUserSessionRegistry;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.registry.RedisGameRoomSessionRegistry;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LobbyDataService {

    private final RedisUserSessionRegistry redisUserSessionRegistry;
    private final RedisGameRoomSessionRegistry redisGameRoomSessionRegistry;

    public LobbyUserListPubDto getCurrentUsers() {
        List<UserSession> userSessions = redisUserSessionRegistry.getAllConnectedUsers();

        List<LobbyUserDto> users = userSessions.stream()
                .map(us-> LobbyUserDto.builder()
                        .memberId(us.getMemberId())
                        .nickname(us.getNickname())
                        .build()).toList();

        int totalCount = users.size();

        return LobbyUserListPubDto.builder().users(users).totalCount(totalCount).build();
    }

    public List<LobbyGameRoomPubDto> getCurrentRooms() {
        List<GameRoomSession> gameRooms = redisGameRoomSessionRegistry.getAllRooms();

        return gameRooms.stream().map(room -> LobbyGameRoomPubDto.builder()
                        .roomId(room.getRoomId())
                        .title(room.getTitle())
                        .status(String.valueOf(room.getStatus()))
                        .currentParticipants(room.getParticipantsCount())
                        .maxParticipants(room.getMaxCount())
                        .build())
                .toList();
    }
}