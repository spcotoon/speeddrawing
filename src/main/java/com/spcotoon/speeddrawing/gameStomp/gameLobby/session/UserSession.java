package com.spcotoon.speeddrawing.gameStomp.gameLobby.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {
    private String sessionId;
    private String nickname;
    private Long memberId;
    private String purpose;
    private String roomId;
}
