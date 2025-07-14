package com.spcotoon.speeddrawing.webtoon.repository;

import com.spcotoon.speeddrawing.webtoon.domain.ComicBody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WebtoonBodyJpaRepository extends JpaRepository<ComicBody, Long> {
}
