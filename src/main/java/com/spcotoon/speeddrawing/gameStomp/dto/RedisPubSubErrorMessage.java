package com.spcotoon.speeddrawing.gameStomp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisPubSubErrorMessage {
    private String sessionId;
    private String message;
}
