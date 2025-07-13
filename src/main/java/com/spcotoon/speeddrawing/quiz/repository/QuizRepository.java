package com.spcotoon.speeddrawing.quiz.repository;

import com.spcotoon.speeddrawing.quiz.domain.Quiz;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuizRepository {
    List<Quiz> findRandomQuizzes(int count);

}
