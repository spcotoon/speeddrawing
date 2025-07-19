package com.spcotoon.speeddrawing.webtoon.service;

import com.spcotoon.speeddrawing.exception.custom.NotExistWebtoonException;
import com.spcotoon.speeddrawing.webtoon.domain.ComicBody;
import com.spcotoon.speeddrawing.webtoon.domain.ComicHead;
import com.spcotoon.speeddrawing.webtoon.dto.admin.WebtoonBodyUploadRespDto;
import com.spcotoon.speeddrawing.webtoon.dto.user.WebtoonContentResponse;
import com.spcotoon.speeddrawing.webtoon.dto.user.WebtoonListResponse;
import com.spcotoon.speeddrawing.webtoon.dto.user.WebtoonTitleResponse;
import com.spcotoon.speeddrawing.webtoon.repository.WebtoonBodyJpaRepository;
import com.spcotoon.speeddrawing.webtoon.repository.WebtoonBodyRepository;
import com.spcotoon.speeddrawing.webtoon.repository.WebtoonHeadJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebtoonService {

    private final WebtoonHeadJpaRepository headRepository;
    private final WebtoonBodyJpaRepository bodyRepository;
    private final WebtoonBodyRepository bodyMybatisRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String now() {
        return LocalDateTime.now().format(FORMATTER);
    }

    public List<WebtoonListResponse> getAllWebtoons(HttpServletRequest request) {

        String clientIp = request.getHeader("X-Forwarded-For");
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        List<ComicHead> comicHeads = headRepository.findAll();

        log.info("웹툰 리스트 열람 | 시간: {} | IP: {} | User-Agent: {} | Referer: {}",
                now(), clientIp, userAgent, referer);

        return comicHeads.stream().map(WebtoonListResponse::from).toList();
    }

    public WebtoonTitleResponse getOneWebtoonTitle(Long comicHeadId, HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");


        ComicHead comicHead = headRepository.findById(comicHeadId).orElseThrow(() -> {
            log.warn("존재하지 않는 웹툰 요청 | 시간: {} | 웹툰 ID: {}", now(), comicHeadId);
            return new NotExistWebtoonException();
        });

        log.info("웹툰 타이틀 열람 | 시간: {} | 웹툰 ID: {} | 웹툰 제목: {} | IP: {} | User-Agent: {} | Referer: {}", now(), comicHeadId, comicHead.getTitle(), clientIp, userAgent, referer);

        return WebtoonTitleResponse.from(comicHead);
    }

    public List<WebtoonBodyUploadRespDto> getOneWebtoonList(Long comicHeadId) {

        return bodyMybatisRepository.findAllByHeadIdOrderByUpdatedDateDesc(comicHeadId);
    }

    public WebtoonContentResponse getOneWebtoon(Long comicHeadId, Long number, HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        ComicBody comicBody = bodyRepository.findOneByComicHeadIdAndNumber(comicHeadId, number).orElseThrow(() -> {
            log.warn("존재하지 않는 웹툰 요청 | 시간: {} | 웹툰 ID: {} | 회차: {}", now(), comicHeadId, number);
            return new NotExistWebtoonException();
        });

        Long maxNumber = bodyRepository.findMaxNumberByComicHeadId(comicHeadId);

        WebtoonContentResponse response = WebtoonContentResponse.from(comicBody);

        response.setMaxNumber(maxNumber);


        log.info("웹툰 특정 작품 열람 | 시간: {} | 웹툰 ID: {} |  회차 넘버: {} | 회차 제목: {} | IP: {} | User-Agent: {} | Referer: {}", now(), comicHeadId, number, comicBody.getTitle(), clientIp, userAgent, referer);

        return response;
    }


}
