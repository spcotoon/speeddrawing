package com.spcotoon.speeddrawing.exception.custom;

public class NotExistWebtoonException extends CustomException{

    private final static String MESSAGE = "존재하지 않는 웹툰입니다.";

    public NotExistWebtoonException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
