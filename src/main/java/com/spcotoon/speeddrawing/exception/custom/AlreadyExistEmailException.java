package com.spcotoon.speeddrawing.exception.custom;

public class AlreadyExistEmailException extends CustomException{

    private final static String MESSAGE = "이미 사용중인 이메일입니다.";

    public AlreadyExistEmailException() {
        super(MESSAGE);
        this.addValidation("email", "duplicate");
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
