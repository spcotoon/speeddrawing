package com.spcotoon.speeddrawing.webtoon.dto.user;

import com.spcotoon.speeddrawing.webtoon.domain.ComicBody;
import com.spcotoon.speeddrawing.webtoon.domain.ContentImageUrl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebtoonContentResponse {
    private Long headId;
    private Long id;
    private Long number;
    private String title;
    private String thumbnailUrl;
    private String authorComment;
    @Builder.Default
    private List<String> contentImageUrls = new ArrayList<>();

    private Long maxNumber;

    public static WebtoonContentResponse from(ComicBody comicBody) {
        return WebtoonContentResponse.builder()
                .headId(comicBody.getComicHead().getId())
                .id(comicBody.getId())
                .number(comicBody.getNumber())
                .title(comicBody.getTitle())
                .thumbnailUrl(comicBody.getThumbnailUrl())
                .authorComment(comicBody.getAuthorComment())
                .contentImageUrls(comicBody.getContentImageUrls().stream().map(ContentImageUrl::getUrl).toList())
                .build();
    }
}
