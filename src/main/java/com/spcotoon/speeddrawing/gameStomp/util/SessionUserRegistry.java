package com.spcotoon.speeddrawing.gameStomp.util;

import com.spcotoon.speeddrawing.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SessionUserRegistry {

    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();

    public void registerUser(String sessionId, String nickname) {
        sessionUserMap.put(sessionId, nickname);
    }

    public void unregisterUser(String sessionId) {
        sessionUserMap.remove(sessionId);
    }

    public List<String> getAllConnectedUsers() {
        return new ArrayList<>(sessionUserMap.values());
    }
}
