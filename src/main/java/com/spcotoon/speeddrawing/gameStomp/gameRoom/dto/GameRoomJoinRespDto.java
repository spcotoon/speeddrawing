package com.spcotoon.speeddrawing.gameStomp.gameRoom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameRoomJoinRespDto {
    private String roomId;
    private String nickname;
}
