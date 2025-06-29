package com.spcotoon.speeddrawing.gameStomp.gameLobby.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSession {
    private Long roomId;
    private List<Long> quizIds;
    private Integer currentIndex;
    private String currentAnswer;
    private Long currentPainterId;
    private Map<Long, Integer> scores;
}
