package com.spcotoon.speeddrawing.gameStomp.gameRoom.registry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spcotoon.speeddrawing.exception.custom.FullRoomException;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisGameRoomSessionRegistry {

    private static final String KEY = "game-room";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void createRoom(GameRoomSession gameRoomSession) {
        try {
            String json = objectMapper.writeValueAsString(gameRoomSession);
            redisTemplate.opsForHash().put(KEY, gameRoomSession.getRoomId(), json);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<GameRoomSession> getAllRooms() {
        Map<Object, Object> all = redisTemplate.opsForHash().entries(KEY);
        return all.values().stream()
                .map(o -> {
                    try {
                        return objectMapper.readValue(o.toString(), GameRoomSession.class);
                    } catch (IOException e) {
                        log.error("Failed to deserialize GameRoomSession");
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    public GameRoomSession getRoom(String roomId) {
        Object value = redisTemplate.opsForHash().get(KEY, roomId);
        if (value == null) return null;

        try {
            return objectMapper.readValue(value.toString(), GameRoomSession.class);
        } catch (IOException e) {
            log.error("Failed to deserialize GameRoomSession");
            throw new RuntimeException(e);
        }
    }

    public void joinRoom(String roomId, String nickname) {
        Object value = redisTemplate.opsForHash().get(KEY, roomId);

        try {
            GameRoomSession session = objectMapper.readValue(value.toString(), GameRoomSession.class);

            if (session.getParticipants() == null) {
                session.setParticipants(new ArrayList<>());
            }

            int currentCount = session.getParticipants().size();


            if (currentCount >= session.getMaxCount()) {
                throw new FullRoomException();
            }

            if (!session.getParticipants().contains(nickname)) {
                session.getParticipants().add(nickname);
                session.setParticipantsCount(session.getParticipants().size());

                if (session.getScore() == null) {
                    session.setScore(new HashMap<>());
                }
                session.getScore().put(nickname, 0);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String now = LocalDateTime.now().format(formatter);


            if (session.getJoinedAt() == null) {
                session.setJoinedAt(new HashMap<>());
            }

            if (!session.getJoinedAt().containsKey(nickname)) {
                session.getJoinedAt().put(nickname, now);
            }

            String updatedJson = objectMapper.writeValueAsString(session);
            redisTemplate.opsForHash().put(KEY, roomId, updatedJson);

        } catch (JsonProcessingException e) {
            log.error("Failed to join game room: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void leaveRoom(String roomId, String nickname) {
        Object value = redisTemplate.opsForHash().get(KEY, roomId);
        if (value == null) {
            log.warn("방이 존재하지 않습니다. roomId={}", roomId);
            return; // 방이 없으면 그냥 종료
        }

        try {
            GameRoomSession session = objectMapper.readValue(value.toString(), GameRoomSession.class);

            if (session.getParticipants() != null && session.getParticipants().contains(nickname)) {
                session.getParticipants().remove(nickname);
                session.getJoinedAt().remove(nickname);
                session.getScore().remove(nickname);
                session.setParticipantsCount(session.getParticipants().size());

                if (nickname.equals(session.getRoomOwner())) {
                    if (session.getParticipantsCount() > 0) {
                        // joinedAt 중 가장 먼저 들어온 사람 찾기
                        String newOwner = session.getParticipants().stream()
                                .min(Comparator.comparing(participant -> session.getJoinedAt().get(participant)))
                                .orElse(null);

                        session.setRoomOwner(newOwner);
                        log.info("방장 교체됨 - newOwner={} in roomId={}", newOwner, roomId);
                    } else {
                        session.setRoomOwner(null);
                        log.info("방장 교체 불가 (참가자 없음) roomId={}", roomId);
                    }
                }


                if (session.getParticipantsCount() == 0) {
                    // 참가자가 0명이면 방 삭제
                    redisTemplate.opsForHash().delete(KEY, roomId);
                    log.info("빈 방 삭제 roomId={}", roomId);
                } else {
                    // 참가자 남아있으면 업데이트
                    String updatedJson = objectMapper.writeValueAsString(session);
                    redisTemplate.opsForHash().put(KEY, roomId, updatedJson);
                    log.info("방에서 퇴장 처리 roomId={} nickname={}", roomId, nickname);
                }
            } else {
                log.warn("방에 참가자가 없거나 해당 닉네임이 존재하지 않음 roomId={}, nickname={}", roomId, nickname);
            }

        } catch (JsonProcessingException e) {
            log.error("leaveRoom 처리 실패 roomId={}, nickname={}, error={}", roomId, nickname, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void updateRoom(GameRoomSession session) {
        try {
            String json = objectMapper.writeValueAsString(session);
            redisTemplate.opsForHash().put(KEY, session.getRoomId(), json);
        } catch (JsonProcessingException e) {
            log.error("Failed to save GameRoomSession");
            throw new RuntimeException(e);
        }
    }

}


