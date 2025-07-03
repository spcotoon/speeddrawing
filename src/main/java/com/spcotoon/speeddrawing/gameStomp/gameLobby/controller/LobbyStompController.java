package com.spcotoon.speeddrawing.gameStomp.gameLobby.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.LobbyDataService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.RedisLobbyPushService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LobbyStompController {

    private final LobbyDataService dataService;
    private final RedisLobbyPushService pushService;

    @MessageMapping("/lobby/init")
    public void initLobby() throws JsonProcessingException {
        pushService.publishRoomList(dataService.getCurrentRooms());
        pushService.publishUserList(dataService.getCurrentUsers());
    }

   
}
