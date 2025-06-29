package com.spcotoon.speeddrawing.gameStomp.gameLobby.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LobbyUserDto {
    private String email;
    private String nickname;
}
