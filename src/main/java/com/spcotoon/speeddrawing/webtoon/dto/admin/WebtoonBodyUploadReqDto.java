package com.spcotoon.speeddrawing.webtoon.dto.admin;

import lombok.Data;

import java.util.List;

@Data
public class WebtoonBodyUploadReqDto {

    private String headId;
    private String title;
    private String authorComment;
    private String thumbnailUrl;
    private List<String> contentUrls;
}
