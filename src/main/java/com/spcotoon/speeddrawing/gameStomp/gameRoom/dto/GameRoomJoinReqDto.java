package com.spcotoon.speeddrawing.gameStomp.gameRoom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameRoomJoinReqDto {
    private String roomId;
    private String nickname;
}
