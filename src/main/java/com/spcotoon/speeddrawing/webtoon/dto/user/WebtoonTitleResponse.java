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
public class WebtoonTitleResponse {
    private Long id;
    private String title;
    private String synopsis;
    private String thumbnailUrl;

    public static WebtoonTitleResponse from(ComicHead comicHead) {
        return WebtoonTitleResponse.builder()
                .id(comicHead.getId())
                .title(comicHead.getTitle())
                .synopsis(comicHead.getSynopsis())
                .thumbnailUrl(comicHead.getThumbnailUrl())
                .build();
    }
}
