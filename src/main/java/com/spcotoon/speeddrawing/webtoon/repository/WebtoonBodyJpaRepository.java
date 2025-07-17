package com.spcotoon.speeddrawing.webtoon.repository;

import com.spcotoon.speeddrawing.webtoon.domain.ComicBody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface WebtoonBodyJpaRepository extends JpaRepository<ComicBody, Long> {

    @Query("SELECT COALESCE(MAX(cb.number), 0) FROM ComicBody cb WHERE cb.comicHead.id = :comicHeadId")
    Long findMaxNumberByComicHeadId(@Param("comicHeadId") Long comicHeadId);

    @Query("""
    SELECT cb
    FROM ComicBody cb
    WHERE cb.comicHead.id = :comicHeadId
      AND cb.number = :number
    """)
    Optional<ComicBody> findOneByComicHeadIdAndNumber(
            @Param("comicHeadId") Long comicHeadId,
            @Param("number") Long number
    );
}
