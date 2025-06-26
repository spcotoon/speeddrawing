package com.spcotoon.speeddrawing.exception.custom;

public class AlreadyExistException extends CustomException{

    private final static String MESSAGE = "이미 사용중입니다.";

    public AlreadyExistException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
