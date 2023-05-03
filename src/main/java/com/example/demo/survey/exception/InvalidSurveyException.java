package com.example.demo.survey.exception;

public class InvalidSurveyException extends RuntimeException {
    private static final String MESSAGE = "올바르지 않은 Survey 파일입니다.";

    public InvalidSurveyException() {
        super(MESSAGE);
    }
}

