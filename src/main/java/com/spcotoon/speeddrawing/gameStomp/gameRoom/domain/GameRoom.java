package com.spcotoon.speeddrawing.gameStomp.gameRoom.domain;

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

    private String name;

    @OneToMany(mappedBy = "gameRoom", cascade = CascadeType.REMOVE)
    private final List<GameRoomParticipant> gameRoomParticipants = new ArrayList<>();

    @OneToMany(mappedBy = "gameRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<GameRoomMessage> gameRoomMessages =new ArrayList<>();
}
