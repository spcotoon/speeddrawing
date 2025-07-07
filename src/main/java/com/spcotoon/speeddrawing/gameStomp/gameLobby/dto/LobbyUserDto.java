package com.spcotoon.speeddrawing.gameStomp.gameLobby.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LobbyUserDto {
    private Long memberId;
    private String email;
    private String nickname;
    private String purpose;
}
