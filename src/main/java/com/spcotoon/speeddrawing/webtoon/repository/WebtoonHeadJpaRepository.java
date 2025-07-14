package com.spcotoon.speeddrawing.webtoon.repository;

import com.spcotoon.speeddrawing.webtoon.domain.ComicHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebtoonHeadJpaRepository extends JpaRepository<ComicHead, Long> {
}
