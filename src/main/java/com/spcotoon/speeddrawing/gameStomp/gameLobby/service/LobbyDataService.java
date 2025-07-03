package com.spcotoon.speeddrawing.gameStomp.gameLobby.service;

import com.spcotoon.speeddrawing.gameStomp.gameLobby.dto.LobbyGameRoomPubDto;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.dto.LobbyUserDto;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.dto.LobbyUserListPubDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.domain.GameRoom;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.repository.GameRoomRepository;
import com.spcotoon.speeddrawing.gameStomp.util.RedisSessionUserRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LobbyDataService {

    private final RedisSessionUserRegistry redisSessionUserRegistry;
    private final GameRoomRepository gameRoomRepository;

    public LobbyUserListPubDto getCurrentUsers() {
        List<LobbyUserDto> users = redisSessionUserRegistry.getAllConnectedUsers().stream()
                .map(nickname -> new LobbyUserDto(null, nickname))
                .collect(Collectors.toList());

        int totalCount = users.size();

        return LobbyUserListPubDto.builder().users(users).totalCount(totalCount).build();
    }

    public List<LobbyGameRoomPubDto> getCurrentRooms() {
        List<GameRoom> gameRooms = gameRoomRepository.findAll();

        return gameRooms.stream().map(room -> LobbyGameRoomPubDto.builder()
                .roomId(room.getId())
                .title(room.getTitle())
                .status(String.valueOf(room.getStatus()))
//                .currentParticipants(room.getGameRoomParticipants().size())
                .maxParticipants(room.getMaxParticipants()).build()).toList();
    }
}