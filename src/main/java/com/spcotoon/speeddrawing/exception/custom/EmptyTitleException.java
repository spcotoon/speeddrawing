package com.spcotoon.speeddrawing.exception.custom;

public class EmptyTitleException extends CustomException{

    private final static String MESSAGE = "제목이 비어있습니다.";

    public EmptyTitleException() {
        super(MESSAGE);
        this.addValidation("title", "제목은 필수 입니다.");
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
