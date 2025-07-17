package com.spcotoon.speeddrawing.webtoon.dto.admin;

import com.spcotoon.speeddrawing.webtoon.domain.ComicBody;
import com.spcotoon.speeddrawing.webtoon.domain.ContentImageUrl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebtoonBodyContentsUploadRespDto {
    private Long id;
    private Long number;
    private String title;
    private String authorComment;
    private List<String> contentImageUrls;

    public static WebtoonBodyContentsUploadRespDto from(ComicBody comicBody) {
        return WebtoonBodyContentsUploadRespDto.builder()
                .id(comicBody.getId())
                .number(comicBody.getNumber())
                .title(comicBody.getTitle())
                .authorComment(comicBody.getAuthorComment())
                .contentImageUrls(comicBody.getContentImageUrls().stream().map(ContentImageUrl::getUrl).toList())
                .build();
    }
}
