package com.spcotoon.speeddrawing.webtoon.dto.user;

import com.spcotoon.speeddrawing.webtoon.domain.ComicHead;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebtoonListResponse {
    private Long comicHeadId;
    private String thumbnailUrl;
    private String title;

    public static WebtoonListResponse from(ComicHead comicHead) {
        return WebtoonListResponse.builder()
                .comicHeadId(comicHead.getId())
                .thumbnailUrl(comicHead.getThumbnailUrl())
                .title(comicHead.getTitle())
                .build();
    }
}
