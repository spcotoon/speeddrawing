package com.spcotoon.speeddrawing.gameStomp.gameRoomChat.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameRoomChatMessageDto {

    @Size(max = 20, message = "20자 까지 입력 가능.")
    private String nickname;
    @Size(max = 50, message = "50자 까지 입력 가능.")
    private String content;
    private GameChatType type;
}
