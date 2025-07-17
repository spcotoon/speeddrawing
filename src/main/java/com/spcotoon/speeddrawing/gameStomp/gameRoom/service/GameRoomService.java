package com.spcotoon.speeddrawing.gameStomp.gameRoom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spcotoon.speeddrawing.common.auth.JwtTokenProvider;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.registry.RedisUserSessionRegistry;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.LobbyDataService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.service.RedisLobbyPushService;
import com.spcotoon.speeddrawing.gameStomp.gameLobby.session.UserSession;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomCreateReqDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.dto.GameRoomJoinReqDto;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.registry.RedisGameRoomSessionRegistry;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomSession;
import com.spcotoon.speeddrawing.gameStomp.gameRoom.session.GameRoomStatus;
import com.spcotoon.speeddrawing.gameStomp.gameRoomChat.dto.GameChatType;
import com.spcotoon.speeddrawing.gameStomp.gameRoomChat.dto.GameRoomChatMessageDto;
import com.spcotoon.speeddrawing.quiz.domain.Quiz;
import com.spcotoon.speeddrawing.quiz.repository.QuizRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final RedisGameRoomSessionRegistry gameSessionRegistry;
    private final RedisUserSessionRegistry userRegistry;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisLobbyPushService lobbyPushService;
    private final LobbyDataService lobbyDataService;
    private final GameSessionPushService gameSessionPushService;
    private final GameSessionDataService gameSessionDataService;
    private final QuizRepository quizRepository;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public String create(GameRoomCreateReqDto dto, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String nickname = "";

        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            nickname = jwtTokenProvider.getNicknameFromToken(token);

        }

        String roomId = String.valueOf(System.currentTimeMillis());

        GameRoomSession gameRoomSession = GameRoomSession.builder()
                .roomId(roomId)
                .title(dto.getTitle())
                .status(GameRoomStatus.WAITING)
                .participantsCount(0)
                .maxCount(4)
                .roomOwner(nickname)
                .build();

        log.info("방 생성 - title={} | createdBy={} | IP: {} | User-Agent: {} | Referer: {}", gameRoomSession.getTitle(), nickname, clientIp, userAgent, referer);
        gameSessionRegistry.createRoom(gameRoomSession);
        try {
            lobbyPushService.publishRoomList(lobbyDataService.getCurrentRooms());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return gameRoomSession.getRoomId();
    }

    public UserSession joinRoom(GameRoomJoinReqDto gameRoomJoinReqDto, HttpServletRequest request) throws JsonProcessingException {
        String authHeader = request.getHeader("Authorization");
        String nickname = "";
        Long memberId = null;
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            nickname = jwtTokenProvider.getNicknameFromToken(token);
            memberId = jwtTokenProvider.getUserInfoFromToken(token).getMemberId();
        } else {
            nickname = gameRoomJoinReqDto.getNickname();
        }


        UserSession userSession = UserSession.builder()
                .nickname(nickname)
                .memberId(memberId)
                .purpose("game-room")
                .roomId(gameRoomJoinReqDto.getRoomId())
                .build();
        userRegistry.registerUser(userSession);
        gameSessionRegistry.joinRoom(gameRoomJoinReqDto.getRoomId(), nickname);
        gameSessionRegistry.getRoom(gameRoomJoinReqDto.getRoomId());
        lobbyPushService.publishUserList(lobbyDataService.getCurrentUsers());
        lobbyPushService.publishRoomList(lobbyDataService.getCurrentRooms());
        gameSessionPushService.publishGameSession(gameSessionDataService.getCurrentGameSession(gameRoomJoinReqDto.getRoomId()));

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        log.info("[{}] 게임방 참가 | roomId={} | nickname={} | IP={} | UA={} | Referer={}",
                now,
                gameRoomJoinReqDto.getRoomId(),
                nickname,
                clientIp,
                userAgent,
                referer
        );

        return userSession;
    }

    public void startGame(String roomId) throws JsonProcessingException {
        GameRoomSession session = gameSessionRegistry.getRoom(roomId);
        if(session==null) throw new RuntimeException("방이 존재하지 않습니다");

        List<String> quizList = quizRepository.findRandomQuizzes(10).stream().map(Quiz::getWord).toList();

        session.setQuizList(quizList);
        session.setQuizTimeLimitSeconds(60);
        session.setCurrentTurnIndex(0);
        session.setTurnStartTimestamp(System.currentTimeMillis());
        session.setStatus(GameRoomStatus.PLAYING);

        List<String> sortedParticipants = session.getParticipants().stream()
                .sorted(Comparator.comparing(nickname -> session.getJoinedAt().get(nickname)))
                .toList();

        String firstDrawer = sortedParticipants.get(0);


        session.setCurrentQuizDrawer(firstDrawer);
        session.setCurrentQuizIndex(0);
        session.setCurrentQuiz(quizList.get(session.getCurrentQuizIndex()));
        session.setStatus(GameRoomStatus.PLAYING);

        gameSessionRegistry.updateRoom(session);
        lobbyPushService.publishRoomList(lobbyDataService.getCurrentRooms());
        gameSessionPushService.publishGameSession(gameSessionDataService.getCurrentGameSession(roomId));
    }

    public void nextTurn(String roomId, String correctUserNickname) throws JsonProcessingException {

        GameRoomSession session = gameSessionRegistry.getRoom(roomId);
        if(session == null) throw new RuntimeException("방이 존재하지 않습니다");

        String currentAnswer = session.getCurrentQuiz();

        List<String> sortedParticipants = session.getParticipants().stream()
                .sorted(Comparator.comparing(nickname -> session.getJoinedAt().get(nickname)))
                .toList();

        int nextTurnIndex = (session.getCurrentTurnIndex() + 1) % sortedParticipants.size();
        String nextDrawer = sortedParticipants.get(nextTurnIndex);

        int quizIndex = session.getCurrentQuizIndex() + 1;
        String nextQuiz = quizIndex < session.getQuizList().size()
                ? session.getQuizList().get(quizIndex)
                : null;

        session.setCurrentTurnIndex(nextTurnIndex);
        session.setCurrentQuizIndex(quizIndex);
        session.setCurrentQuizDrawer(nextDrawer);
        session.setCurrentQuiz(nextQuiz);
        session.setTurnStartTimestamp(System.currentTimeMillis());

        if (nextQuiz == null) {
            finishGame(session);
            return;
        }

        gameSessionRegistry.updateRoom(session);

        lobbyPushService.publishRoomList(lobbyDataService.getCurrentRooms());
        gameSessionPushService.publishGameSession(gameSessionDataService.getCurrentGameSession(roomId));

        String correctUserText = (correctUserNickname != null)
                ? correctUserNickname
                : "없음";

        GameRoomChatMessageDto answerMessage = new GameRoomChatMessageDto();
        answerMessage.setNickname("SYSTEM");
        answerMessage.setType(GameChatType.ANSWER_CORRECT);
        answerMessage.setContent(String.format(
                "정답: %s / 정답자: %s / 다음 출제자: %s",
                currentAnswer,
                correctUserText,
                nextDrawer
        ));

        String channel = "gameChat:" + roomId;
        String json = objectMapper.writeValueAsString(answerMessage);
        redisTemplate.convertAndSend(channel, json);
    }

    public void finishGame(GameRoomSession session) throws JsonProcessingException {
        session.setStatus(GameRoomStatus.WAITING);
        session.setCurrentQuiz(null);
        session.setCurrentQuizDrawer(null);
        session.setQuizList(null);
        session.setCurrentQuizIndex(0);
        session.setCurrentTurnIndex(0);
        session.setTurnStartTimestamp(0);

        // 점수 정렬 & 순위 매기기
        Map<String, Integer> scores = session.getScore();
        List<Map.Entry<String, Integer>> sortedScores = scores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .toList();

        // 순위 메시지 조립
        StringBuilder rankMessage = new StringBuilder("게임 종료! 최종 순위:\n");
        int rank = 1;
        for (Map.Entry<String, Integer> entry : sortedScores) {
            rankMessage.append(String.format("%d. %s - %d점 | ", rank++, entry.getKey(), entry.getValue()));
        }
        if (sortedScores.isEmpty()) {
            rankMessage.append("참가자가 없습니다.");
        }

        for (Map.Entry<String, Integer> entry : session.getScore().entrySet()) {
            entry.setValue(0);
        }

        gameSessionRegistry.updateRoom(session);
        lobbyPushService.publishRoomList(lobbyDataService.getCurrentRooms());
        gameSessionPushService.publishGameSession(gameSessionDataService.getCurrentGameSession(session.getRoomId()));

        GameRoomChatMessageDto finishMessage = new GameRoomChatMessageDto();
        finishMessage.setNickname("SYSTEM");
        finishMessage.setType(GameChatType.SYSTEM);
        finishMessage.setContent(rankMessage.toString());

        String channel = "gameChat:" + session.getRoomId();
        String json = objectMapper.writeValueAsString(finishMessage);
        redisTemplate.convertAndSend(channel, json);
    }


}