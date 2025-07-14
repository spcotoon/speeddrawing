package com.spcotoon.speeddrawing.webtoon.dto;

import com.spcotoon.speeddrawing.webtoon.domain.ComicHead;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebtoonHeadUploadRespDto {
    private Long id;
    private String title;
    private String synopsis;
    private String thumbnailUrl;

    public static WebtoonHeadUploadRespDto from(ComicHead comicHead) {
        return WebtoonHeadUploadRespDto.builder()
                .id(comicHead.getId())
                .title(comicHead.getTitle())
                .synopsis(comicHead.getSynopsis())
                .thumbnailUrl(comicHead.getThumbnailUrl())
                .build();
    }
}
