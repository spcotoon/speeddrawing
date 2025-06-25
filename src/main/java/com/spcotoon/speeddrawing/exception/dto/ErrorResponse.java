package com.spcotoon.speeddrawing.exception.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;
    private final List<ValidationTuple> validations;

    public void addValidation(String fieldName, String message) {
        this.validations.add(new ValidationTuple(fieldName, message));
    }

    @Builder
    public ErrorResponse(String code, String message, List<ValidationTuple> validations) {
        this.code = code;
        this.message = message;
        this.validations = validations != null ? validations : new ArrayList<>();
    }
}
