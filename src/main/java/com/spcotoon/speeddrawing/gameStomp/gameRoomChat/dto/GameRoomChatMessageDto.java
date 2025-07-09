package com.spcotoon.speeddrawing.gameStomp.gameRoomChat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameRoomChatMessageDto {

    private String nickname;
    private String content;
}
