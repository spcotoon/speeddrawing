package com.spcotoon.speeddrawing.webtoon.domain;


import com.spcotoon.speeddrawing.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ComicHead extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comic_head_id")
    private Long id;

    private String title;
    private String synopsis;
    private String thumbnailUrl;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comicHead")
    private List<ComicBody> comicBodies = new ArrayList<>();

    @Builder
    public ComicHead(String title, String synopsis, String thumbnailUrl) {
        this.title = title;
        this.synopsis = synopsis;
        this.thumbnailUrl = thumbnailUrl;
    }
}
