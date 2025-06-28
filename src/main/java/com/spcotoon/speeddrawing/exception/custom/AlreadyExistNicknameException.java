package com.spcotoon.speeddrawing.exception.custom;

public class AlreadyExistNicknameException extends CustomException{

    private final static String MESSAGE = "이미 사용중인 닉네임입니다.";

    public AlreadyExistNicknameException() {
        super(MESSAGE);
        this.addValidation("nickname", "duplicate");
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
