package com.spcotoon.speeddrawing.gameStomp.gameRoom.domain;

public enum GameRoomStatus {
    WAITING("WAITING", "대기중"),
    PLAYING("PLAYING", "게임중");

    private final String key;
    private final String description;

    GameRoomStatus(String key, String description) {
        this.key = key;
        this.description = description;
    }
}
