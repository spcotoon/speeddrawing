package com.spcotoon.speeddrawing.gameStomp.gameRoom.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameRoomSession {

    private String roomId;
    private String title;
    private GameRoomStatus status;
    private int participantsCount;
    private int maxCount;
}