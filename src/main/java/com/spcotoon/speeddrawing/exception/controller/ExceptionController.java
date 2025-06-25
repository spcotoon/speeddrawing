package com.spcotoon.speeddrawing.exception.controller;

import com.spcotoon.speeddrawing.exception.custom.CustomException;
import com.spcotoon.speeddrawing.exception.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse notValidRequestHandler(MethodArgumentNotValidException e) {

        ErrorResponse response = ErrorResponse.builder()
                .code("400")
                .message("잘못된 요청입니다.")
                .build();
        e.getFieldErrors().forEach(error -> response.addValidation(error.getField(), error.getDefaultMessage()));

        return response;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponse mismatchRequestHandler(MethodArgumentTypeMismatchException e) {

        return ErrorResponse.builder()
                .code("400")
                .message("잘못된 요청입니다.")
                .build();
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> customExceptionHandler(CustomException e) {
        int statusCode = e.getStatusCode();

        ErrorResponse body = ErrorResponse.builder()
                .code(String.valueOf(statusCode))
                .message(e.getMessage())
                .validations(e.getValidationTuples())
                .build();

        return ResponseEntity.status(statusCode).body(body);
    }
}
