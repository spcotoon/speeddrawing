package com.spcotoon.speeddrawing.webtoon.dto.admin;

import com.spcotoon.speeddrawing.webtoon.domain.ComicHead;
import lombok.Builder;
import lombok.Data;

@Data
public class OneWebtoonHeadRespDto {

    private String headTitle;
    private String headSynopsis;
    private String headThumbnailUrl;

    @Builder
    public OneWebtoonHeadRespDto(String headTitle, String headSynopsis, String headThumbnailUrl) {
        this.headTitle = headTitle;
        this.headSynopsis = headSynopsis;
        this.headThumbnailUrl = headThumbnailUrl;
    }

    public static OneWebtoonHeadRespDto from(ComicHead comicHead) {
        return OneWebtoonHeadRespDto.builder()
                .headTitle(comicHead.getTitle())
                .headSynopsis(comicHead.getSynopsis())
                .headThumbnailUrl(comicHead.getThumbnailUrl())
                .build();
    }
}
