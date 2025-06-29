package com.spcotoon.speeddrawing.gameStomp.gameRoom.repository;

import com.spcotoon.speeddrawing.gameStomp.gameRoom.domain.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {

}
