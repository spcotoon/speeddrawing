package com.spcotoon.speeddrawing.gameStomp.gameRoom.dto;

import com.spcotoon.speeddrawing.gameStomp.gameRoom.domain.GameRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameRoomCreateReqDto {

    private String title;

    public GameRoom toEntity() {
        return new GameRoom(this.title);
    }
}
