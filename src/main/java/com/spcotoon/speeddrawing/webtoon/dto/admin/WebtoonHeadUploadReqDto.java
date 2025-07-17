package com.spcotoon.speeddrawing.webtoon.dto.admin;

import com.spcotoon.speeddrawing.webtoon.domain.ComicHead;
import lombok.Data;

@Data
public class WebtoonHeadUploadReqDto {
    private String title;
    private String synopsis;
    private String genre;
    private String thumbnailUrl;

    public ComicHead toEntity() {
        return ComicHead.builder()
                .title(this.title)
                .synopsis(this.synopsis)
                .thumbnailUrl(this.thumbnailUrl)
                .build();

    }
}
