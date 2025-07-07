package com.spcotoon.speeddrawing.gameStomp.gameRoom.dto;

import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomSession;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameSessionPubDto {

    private String roomId;
    private String title;
    private GameRoomStatus status;
    private int participantsCount;
    private int maxCount;

    private String roomOwner;
    private List<String> participants;
    private int currentRound;
    private int quizIndex;
    private Map<String, Integer> score;

    private Map<String, String> joinedAt;

    public GameSessionPubDto from(GameRoomSession gameRoomSession) {
        return GameSessionPubDto.builder()
                .roomId(gameRoomSession.getRoomId())
                .title(gameRoomSession.getTitle())
                .status(gameRoomSession.getStatus())
                .participantsCount(gameRoomSession.getParticipantsCount())
                .maxCount(gameRoomSession.getMaxCount())
                .roomOwner(gameRoomSession.getRoomOwner())
                .participants(gameRoomSession.getParticipants())
                .currentRound(gameRoomSession.getCurrentRound())
                .quizIndex(gameRoomSession.getQuizIndex())
                .score(gameRoomSession.getScore())
                .joinedAt(gameRoomSession.getJoinedAt())
                .build();
    }
}
