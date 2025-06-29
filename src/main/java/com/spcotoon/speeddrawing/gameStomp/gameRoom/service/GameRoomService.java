package com.spcotoon.speeddrawing.gameStomp.gameRoom.service;

import com.spcotoon.speeddrawing.gameStomp.gameRoom.domain.GameRoom;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomCreateReqDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.repository.GameRoomRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GameRoomService {

    private final GameRoomRepository gameRoomRepository;

    public Long create(GameRoomCreateReqDto dto, HttpServletRequest request) {
        GameRoom gameRoom = dto.toEntity();
        gameRoomRepository.save(gameRoom);

        return gameRoom.getId();
    }

}
