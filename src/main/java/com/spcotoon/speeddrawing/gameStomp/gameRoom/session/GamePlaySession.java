package com.spcotoon.speeddrawing.gameStomp.gameRoom.session;

import com.spcotoon.speeddrawing.gameStomp.gameLobby.session.UserSession;
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
public class GamePlaySession {

    private String roomOwner;

    //레디스 hash로 저장될건데 list를 넣을순 없지않나?
    private List<String> participants;

    private int currentRound;

    private int quizIndex;

    //이거도 마찬가지고
    private Map<String, Integer> score;
}


