package com.spcotoon.speeddrawing.exception.custom;

public class FullRoomException extends CustomException {
    private final static String MESSAGE = "방이 가득 찼습니다.";

    public FullRoomException() {
        super(MESSAGE);
        this.addValidation("participantsCount", "over max count");
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
