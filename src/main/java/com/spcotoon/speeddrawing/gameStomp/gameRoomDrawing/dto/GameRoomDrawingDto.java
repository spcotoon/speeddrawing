package com.spcotoon.speeddrawing.gameStomp.gameRoomDrawing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameRoomDrawingDto {

    private String type; // "stroke", "erase" 등

    private List<Point> points;

    private String color;

    private int lineWidth;

    private String nickname;

    // 내부 static class로 좌표 정의
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Point {
        private int x;
        private int y;
    }
}
