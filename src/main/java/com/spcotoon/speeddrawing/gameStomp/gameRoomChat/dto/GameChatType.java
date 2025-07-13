package com.spcotoon.speeddrawing.gameStomp.gameRoomChat.dto;

public enum GameChatType {
    CHAT("CHAT", "일반채팅"),
    ANSWER_CORRECT("ANSWER_CORRECT", "정답"),
    SYSTEM("SYSTEM", "시스템"),
    GAME_START("START", "시작"),
    FINISHED("FINISHED", "종료");

    private final String key;
    private final String description;

    GameChatType(String key, String description) {
        this.key = key;
        this.description = description;
    }
}
