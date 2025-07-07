package com.spcotoon.speeddrawing.exception.custom;

public class MustLoginException extends CustomException{
    private final static String MESSAGE = "로그인 후 이용해주세요.";

    public MustLoginException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
