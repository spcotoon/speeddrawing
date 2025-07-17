package com.spcotoon.speeddrawing.webtoon.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebtoonBodyUploadRespDto {
    private Long comicHeadId;
    private Long id;
    private Long number;
    private String title;
    private String thumbnailUrl;
    private LocalDateTime updatedTime;
}
