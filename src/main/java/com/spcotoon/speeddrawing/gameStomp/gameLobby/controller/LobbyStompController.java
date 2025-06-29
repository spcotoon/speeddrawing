package com.spcotoon.speeddrawing.gameStomp.gameLobby.controller;

import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.GameLobbyPushService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.LobbyDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LobbyStompController {

    private final GameLobbyPushService pushService;
    private final LobbyDataService dataService;

    @MessageMapping("/lobby/init")
    public void initLobby() {
        pushService.publishRoomListInLobby(dataService.getCurrentRooms());
        pushService.publishUserListInLobby(dataService.getCurrentUsers());
    }
}
