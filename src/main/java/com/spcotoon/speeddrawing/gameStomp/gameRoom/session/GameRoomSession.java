package com.spcotoon.speeddrawing.gameStomp.gameRoom.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

    private String roomOwner;
    private List<String> participants;
    private Map<String, Integer> score;
    private Map<String, String> joinedAt;

    private int quizTimeLimitSeconds;
    private String currentQuizDrawer;
    private List<String> quizList = new ArrayList<>();
    private int currentTurnIndex;
    private int currentQuizIndex;
    private String currentQuiz;

    private long turnStartTimestamp;
}