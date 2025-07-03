package com.spcotoon.speeddrawing.gameStomp.gameRoom.domain;

import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class GameRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_room_id")
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private GameRoomStatus status;

    private final Integer maxParticipants = 4;

    @OneToMany(mappedBy = "gameRoom", cascade = CascadeType.REMOVE)
    private final List<GameRoomParticipant> gameRoomParticipants = new ArrayList<>();

    @OneToMany(mappedBy = "gameRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<GameRoomMessage> gameRoomMessages = new ArrayList<>();

    public GameRoom(String title) {
        this.title = title;
        this.status = GameRoomStatus.WAITING;
    }
}
