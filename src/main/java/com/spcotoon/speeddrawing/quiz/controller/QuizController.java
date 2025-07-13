package com.spcotoon.speeddrawing.quiz.controller;

import com.spcotoon.speeddrawing.quiz.service.QuizExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizExcelService quizExcelService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            quizExcelService.importExcel(file.getInputStream());

            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllQuizzes() {
        return ResponseEntity.ok(quizExcelService.getAllQuizzes());
    }
}
