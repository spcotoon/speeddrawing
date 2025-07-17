package com.spcotoon.speeddrawing.webtoon.controller;

import com.spcotoon.speeddrawing.webtoon.dto.admin.WebtoonBodyUploadRespDto;
import com.spcotoon.speeddrawing.webtoon.dto.user.WebtoonContentResponse;
import com.spcotoon.speeddrawing.webtoon.dto.user.WebtoonListResponse;
import com.spcotoon.speeddrawing.webtoon.dto.user.WebtoonTitleResponse;
import com.spcotoon.speeddrawing.webtoon.service.WebtoonService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/webtoon")
public class WebtoonController {

    private final WebtoonService webtoonService;

    @GetMapping("/list")
    public ResponseEntity<?> getAllWebtoons(HttpServletRequest request) {
        List<WebtoonListResponse> response = webtoonService.getAllWebtoons(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/one-webtoon-title")
    public ResponseEntity<?> getOneWebtoonTitle(@RequestParam Long comicHeadId, HttpServletRequest request) {
        WebtoonTitleResponse response = webtoonService.getOneWebtoonTitle(comicHeadId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/one-webtoon-list")
    public ResponseEntity<?> getOneWebtoonList(@RequestParam Long comicHeadId) {

        List<WebtoonBodyUploadRespDto> response = webtoonService.getOneWebtoonList(comicHeadId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/one")
    public ResponseEntity<?> getOneWebtoon(@RequestParam Long comicHeadId, @RequestParam Long number, HttpServletRequest request) {

        WebtoonContentResponse response = webtoonService.getOneWebtoon(comicHeadId, number, request);

        return ResponseEntity.ok(response);
    }
}
