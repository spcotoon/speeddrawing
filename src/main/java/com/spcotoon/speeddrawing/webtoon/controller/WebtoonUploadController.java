package com.spcotoon.speeddrawing.webtoon.controller;

import com.spcotoon.speeddrawing.webtoon.domain.ComicBody;
import com.spcotoon.speeddrawing.webtoon.domain.ComicHead;
import com.spcotoon.speeddrawing.webtoon.dto.*;
import com.spcotoon.speeddrawing.webtoon.service.S3Service;
import com.spcotoon.speeddrawing.webtoon.service.WebtoonUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/webtoon-upload")
public class WebtoonUploadController {

    private final WebtoonUploadService webtoonUploadService;
    private final S3Service s3Service;

    @PostMapping("/body/upload")
    public ResponseEntity<?> comicBodyList(@RequestBody WebtoonBodyUploadReqDto reqDto) {

        ComicBody comicBody = webtoonUploadService.bodyUpload(reqDto);

        return ResponseEntity.ok(comicBody.getId());
    }


    @GetMapping("/head/one")
    public ResponseEntity<?> getOneComicHead(@RequestParam Long comicHeadId) {

        OneWebtoonHeadRespDto webtoonHead = webtoonUploadService.getOneHead(comicHeadId);

        return ResponseEntity.ok(webtoonHead);
    }

    @GetMapping("/head/list")
    public ResponseEntity<?> getComicHeadList() {

        List<ComicHead> list = webtoonUploadService.getHeadList();
        List<WebtoonHeadUploadRespDto> dto = list.stream().map(WebtoonHeadUploadRespDto::from
        ).toList();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/body/list")
    public ResponseEntity<?> getComicBodyListOfOneHead(@RequestParam Long comicHeadId) {
        List<WebtoonBodyUploadRespDto> response = webtoonUploadService.getBodyList(comicHeadId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/head/upload")
    public ResponseEntity<?> comicUpload(@RequestBody WebtoonHeadUploadReqDto reqDto) {

        ComicHead comicHead = webtoonUploadService.headUpload(reqDto);

        return ResponseEntity.ok(comicHead.getId());
    }


    @PostMapping("/head/presigned-url")
    public ResponseEntity<?> uploadThumbnail(@RequestBody String fileName) {

        String fileExtension = getFileExtension(fileName);
        String uniqueFileName = generateUniqueFileName(fileName, fileExtension);

        String s3Path = "webtoon/" + uniqueFileName;

        String presignedUrl = s3Service.createPresignedUrl(s3Path);

        return ResponseEntity.ok(presignedUrl);
    }

    @PostMapping("/body-thumbnail/presigned-url")
    public ResponseEntity<?> uploadBodyThumbnail(@RequestBody String fileName) {
        String fileExtension = getFileExtension(fileName);
        String uniqueFileName = generateUniqueFileName(fileName, fileExtension);

        String s3Path = "webtoon/" + uniqueFileName;

        String presignedUrl = s3Service.createPresignedUrl(s3Path);

        return ResponseEntity.ok(presignedUrl);
    }

    @PostMapping("/body-contents/presigned-url")
    public ResponseEntity<?> uploadBodyContents(@RequestBody List<String> fileNames) {

        List<String> presignedUrls = new ArrayList<>();

        for (String fileName : fileNames) {
            String fileExtension = getFileExtension(fileName);
            String uniqueFileName = generateUniqueFileName(fileName, fileExtension);

            String s3Path = "comics/" + uniqueFileName;

            String presignedUrl = s3Service.createPresignedUrl(s3Path);
            presignedUrls.add(presignedUrl);

        }
        return ResponseEntity.ok(presignedUrls);
    }

    // 원본 파일명에서 확장자 제외한 이름만 추출
    private String getBaseName(String url) {

        int lastDotIndex = url.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return url.substring(0, lastDotIndex);
        }
        return url;
    }

    // 파일 확장자 추출
    private String getFileExtension(String url) {
        int lastDotIndex = url.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return url.substring(lastDotIndex);
        }
        return "";
    }

    private String generateUniqueFileName(String originalFileName, String fileExtension) {

        String baseName = getBaseName(originalFileName);
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        long nanoTime = System.nanoTime();

        return baseName + "_" + timestamp + "_" + nanoTime + fileExtension;
    }
}
