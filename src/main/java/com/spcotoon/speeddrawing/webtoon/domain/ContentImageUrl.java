package com.spcotoon.speeddrawing.webtoon.domain;

import com.spcotoon.speeddrawing.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentImageUrl extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_image_url_id")
    private Long id;

    private String url;

    @ManyToOne
    @JoinColumn(name = "comic_body_id")
    ComicBody comicBody;

    public ContentImageUrl(String url, ComicBody comicBody) {
        this.url = url;
        this.comicBody = comicBody;
    }
}
