package com.spcotoon.speeddrawing.quiz.repository;

import com.spcotoon.speeddrawing.quiz.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizJpaRepository extends JpaRepository<Quiz, Long> {
}
