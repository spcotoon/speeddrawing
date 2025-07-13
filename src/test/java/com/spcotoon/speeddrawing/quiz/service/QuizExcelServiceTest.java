package com.spcotoon.speeddrawing.quiz.service;

import com.spcotoon.speeddrawing.quiz.repository.QuizJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuizExcelServiceTest {
  @Autowired
  private QuizExcelService quizExcelService;

  @Autowired
  private QuizJpaRepository quizJpaRepository;

    @AfterEach
    void tearDown() {
        quizJpaRepository.deleteAll();
    }

  @DisplayName("엑셀 파일을 읽어와 DB 저장")
  @Test
  void testImportExcel() throws Exception {
      //given
      InputStream is = getClass().getClassLoader().getResourceAsStream("quiz_test.xlsx");

      quizExcelService.importExcel(is);
      long count = quizJpaRepository.count();

      //when
      //then
      assertThat(count).isGreaterThan(0);
      boolean exists = quizJpaRepository.findAll().stream()
              .anyMatch(q -> q.getWord().equalsIgnoreCase("테스트"));
      assertThat(exists).isTrue();

   }
}