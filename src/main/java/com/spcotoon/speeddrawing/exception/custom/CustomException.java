package com.spcotoon.speeddrawing.exception.custom;

import com.spcotoon.speeddrawing.exception.dto.ValidationTuple;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomException extends RuntimeException {

    private final List<ValidationTuple> validationTuples = new ArrayList<>();

    public abstract int getStatusCode();

    public CustomException(String message) {
        super(message);
    }

    public List<ValidationTuple> getValidationTuples() {
        return validationTuples;
    }

    public void addValidation(String fieldName, String message) {
        this.validationTuples.add(new ValidationTuple(fieldName, message));
    }
}
