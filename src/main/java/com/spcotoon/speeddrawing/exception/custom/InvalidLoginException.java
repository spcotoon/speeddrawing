package com.spcotoon.speeddrawing.exception.custom;

public class InvalidLoginException extends CustomException{

    private final static String MESSAGE = "아이디 또는 비밀번호가 틀렸습니다.";

    public InvalidLoginException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
