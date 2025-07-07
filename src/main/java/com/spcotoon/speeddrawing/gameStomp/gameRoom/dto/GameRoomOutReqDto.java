package com.spcotoon.speeddrawing.gameStomp.gameRoom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRoomOutReqDto {
    private String nickname;
    private String roomId;
}
