package com.spcotoon.speeddrawing.webtoon.repository;

import com.spcotoon.speeddrawing.webtoon.dto.admin.WebtoonBodyUploadRespDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WebtoonBodyRepository {
    List<WebtoonBodyUploadRespDto> findAllByHeadIdOrderByUpdatedDateDesc(Long comicHeadId);
}
