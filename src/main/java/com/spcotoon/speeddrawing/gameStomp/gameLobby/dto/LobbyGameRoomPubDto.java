package com.spcotoon.speeddrawing.gameStomp.gameLobby.dto;

import com.spcotoon.speeddrawing.gameStomp.gameRoom.domain.GameRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LobbyGameRoomPubDto {
    private String roomId;
    private String title;
    private String status;
    private Integer currentParticipants;
    private Integer maxParticipants;
}
