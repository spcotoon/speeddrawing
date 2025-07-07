package com.spcotoon.speeddrawing.gameStomp.gameRoom.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spcotoon.speeddrawing.exception.custom.FullRoomException;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.session.UserSession;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomCreateReqDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomJoinReqDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomJoinRespDto;
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

    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestBody GameRoomJoinReqDto gameRoomJoinReqDto, HttpServletRequest request) {
        try {
            UserSession userSession = gameRoomService.joinRoom(gameRoomJoinReqDto, request);
            GameRoomJoinRespDto body = GameRoomJoinRespDto.builder()
                    .nickname(userSession.getNickname())
                    .roomId(userSession.getRoomId())
                    .build();

            return ResponseEntity.ok(body);
        } catch (FullRoomException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
