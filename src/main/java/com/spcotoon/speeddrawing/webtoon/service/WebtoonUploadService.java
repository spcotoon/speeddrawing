package com.spcotoon.speeddrawing.webtoon.service;

import com.spcotoon.speeddrawing.exception.custom.NotExistWebtoonException;
import com.spcotoon.speeddrawing.webtoon.domain.ComicBody;
import com.spcotoon.speeddrawing.webtoon.domain.ComicHead;
import com.spcotoon.speeddrawing.webtoon.domain.ContentImageUrl;
import com.spcotoon.speeddrawing.webtoon.dto.admin.*;
import com.spcotoon.speeddrawing.webtoon.repository.WebtoonBodyJpaRepository;
import com.spcotoon.speeddrawing.webtoon.repository.WebtoonBodyRepository;
import com.spcotoon.speeddrawing.webtoon.repository.WebtoonHeadJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebtoonUploadService {

    private final WebtoonHeadJpaRepository headRepository;
    private final WebtoonBodyJpaRepository bodyRepository;
    private final WebtoonBodyRepository bodyMybatisRepository;

    @Transactional
    public ComicHead headUpload(WebtoonHeadUploadReqDto reqDto) {
        ComicHead comicHead = reqDto.toEntity();
        return headRepository.save(comicHead);
    }

    public List<ComicHead> getHeadList() {

        return headRepository.findAll();
    }

    @Transactional
    public ComicBody bodyUpload(WebtoonBodyUploadReqDto reqDto) {

        ComicHead comicHead = headRepository.findById(Long.valueOf(reqDto.getHeadId())).orElseThrow(NotExistWebtoonException::new);

        Long maxNumber = bodyRepository.findMaxNumberByComicHeadId(comicHead.getId());
        Long nextNumber = (maxNumber == null || maxNumber == 0) ? 1L : maxNumber + 1L;

        ComicBody comicBody = ComicBody.builder()
                .number(nextNumber)
                .title(reqDto.getTitle())
                .authorComment(reqDto.getAuthorComment())
                .thumbnailUrl(reqDto.getThumbnailUrl())
                .comicHead(comicHead)
                .build();

        List<ContentImageUrl> imageUrls = new ArrayList<>();
        for (String url : reqDto.getContentUrls()) {
            ContentImageUrl imageUrl = new ContentImageUrl(url, comicBody);
            imageUrls.add(imageUrl);
        }

        comicBody.getContentImageUrls().addAll(imageUrls);

        return bodyRepository.save(comicBody);
    }

    public WebtoonBodyContentsUploadRespDto getOneBody(Long comicBodyId) {
        ComicBody comicBody = bodyRepository.findById(comicBodyId)
                .orElseThrow(NotExistWebtoonException::new);

        return WebtoonBodyContentsUploadRespDto.from(comicBody);
    }


    public OneWebtoonHeadRespDto getOneHead(Long comicHeadId) {
        ComicHead comicHead = headRepository.findById(comicHeadId).orElseThrow(NotExistWebtoonException::new);

        return OneWebtoonHeadRespDto.from(comicHead);
    }

    public List<WebtoonBodyUploadRespDto> getBodyList(Long comicHeadId) {
        return bodyMybatisRepository.findAllByHeadIdOrderByUpdatedDateDesc(comicHeadId);
    }
}
