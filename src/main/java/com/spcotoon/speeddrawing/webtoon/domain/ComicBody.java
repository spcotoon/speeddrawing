package com.spcotoon.speeddrawing.webtoon.domain;

import com.spcotoon.speeddrawing.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.catalina.LifecycleState;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ComicBody extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comic_body_id")
    private Long id;
    private Long number;
    private String title;
    private String thumbnailUrl;
    private String authorComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comic_head_id")
    private ComicHead comicHead;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comicBody")
    private List<ContentImageUrl> contentImageUrls = new ArrayList<>();

    @Builder
    public ComicBody(Long number, String title, String thumbnailUrl, String authorComment, List<String> contentUrl, ComicHead comicHead) {
        this.number = number;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.authorComment = authorComment;
        this.comicHead = comicHead;
    }
}
