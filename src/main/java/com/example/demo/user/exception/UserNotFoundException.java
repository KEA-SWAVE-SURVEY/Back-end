package com.example.demo.user.exception;

/**
 * @description 유저 정보 조회 실패 > error throw
 * @author 김기현
 * @since 2023.04.24
 */
public class UserNotFoundException extends RuntimeException{
    private static final String MESSAGE = "회원 정보를 찾을 수 없습니다";

    public UserNotFoundException() {
        super(MESSAGE);
    }
}
