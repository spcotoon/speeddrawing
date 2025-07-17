package com.spcotoon.speeddrawing.gameStomp.gameRoom.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spcotoon.speeddrawing.exception.custom.FullRoomException;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.session.UserSession;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomCreateReqDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomJoinReqDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomJoinRespDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.service.GameRoomService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/game_room")
@RequiredArgsConstructor
public class GameRoomController {
    private final GameRoomService gameRoomService;


    @Operation(
            summary = "게임 방 생성",
            description = "게임 방을 생성합니다.",
            security = @SecurityRequirement(name = "JWT"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게임 방 생성 JSON 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GameRoomCreateReqDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "방 생성 성공. 생성된 Room ID를 반환합니다.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "요청 데이터가 잘못된 경우"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러"
                    )
            }
    )
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
