package com.spcotoon.speeddrawing.gameStomp.gameLobby.repository;

import com.spcotoon.speeddrawing.gameStomp.gameRoom.domain.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
}
