package com.spcotoon.speeddrawing.quiz.service;

import com.spcotoon.speeddrawing.quiz.domain.Quiz;
import com.spcotoon.speeddrawing.quiz.repository.QuizJpaRepository;
import com.spcotoon.speeddrawing.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizExcelService {

    private final QuizJpaRepository quizJpaRepository;

    @Transactional
    public void importExcel(InputStream excelInputStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(excelInputStream);

        Sheet sheet = workbook.getSheetAt(0);

        List<Quiz> quizzes = new ArrayList<>();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            Cell cell = row.getCell(0);
            if (cell == null) continue;

            String word = cell.getStringCellValue();

            Quiz quiz = new Quiz(word.trim());
            quizzes.add(quiz);
        }

        quizJpaRepository.saveAll(quizzes);

        workbook.close();
    }

    @Transactional(readOnly = true)
    public List<Quiz> getAllQuizzes() {
        return quizJpaRepository.findAll();
    }
}
