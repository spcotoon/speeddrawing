package com.spcotoon.speeddrawing.gameStomp.gameLobby.service;

import com.spcotoon.speeddrawing.gameStomp.gameLobby.dto.LobbyGameRoomPubDto;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.dto.LobbyUserPubDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameLobbyPushService {
    private final SimpMessagingTemplate messagingTemplate;

    public void publishRoomListInLobby(List<LobbyGameRoomPubDto> rooms) {
        messagingTemplate.convertAndSend("/topic/lobby/rooms", rooms);
    }


    public void publishUserListInLobby(List<LobbyUserPubDto> users) {
        messagingTemplate.convertAndSend("/topic/lobby/users", users);
    }
}
