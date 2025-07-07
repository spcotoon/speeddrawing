package com.spcotoon.speeddrawing.gameStomp.gameRoom.service;

import com.spcotoon.speeddrawing.gameStomp.gameLobby.dto.LobbyUserDto;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.dto.LobbyUserListPubDto;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.session.UserSession;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameSessionPubDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.registry.RedisGameRoomSessionRegistry;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameSessionDataService {
    private final RedisGameRoomSessionRegistry gameRoomSessionRegistry;

    public GameSessionPubDto getCurrentGameSession(String roomId) {

        GameRoomSession room = gameRoomSessionRegistry.getRoom(roomId);
        GameSessionPubDto gameSessionPubDto = new GameSessionPubDto();
        return gameSessionPubDto.from(room);
    }
}
