package com.spcotoon.speeddrawing.gameStomp.gameRoom.controller;

import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomCreateReqDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.service.GameRoomService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/game_room")
@RequiredArgsConstructor
public class GameRoomController {
    private final GameRoomService gameRoomService;

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody GameRoomCreateReqDto dto, HttpServletRequest request) {
        String roomId = gameRoomService.create(dto, request);

        return new ResponseEntity<>(roomId, HttpStatus.OK);
    }
}
