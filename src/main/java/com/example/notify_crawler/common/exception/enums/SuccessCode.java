package com.example.notify_crawler.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {


    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 성공입니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공입니다"),
    REGISTER_SUCCESS(HttpStatus.OK, "회원가입 성공"),
    DELETE_SUCCESS(HttpStatus.OK, "회원탈퇴 성공"),
    ADD_KEYWORD_SUCCESS(HttpStatus.OK, "키워드 추가 성공"),
    DELETE_KEYWORD_SUCCESS(HttpStatus.OK, "키워드 삭제 성공"),
    SAVE_SCRAP_SUCCESS(HttpStatus.OK, "스크랩 성공입니다."),
    DELETE_SCRAP_SUCCESS(HttpStatus.OK, "스크랩 제거 성공입니다."),
    GET_SCRAP_SUCCESS(HttpStatus.OK, "스크랩 조회 성공입니다."),
    GET_PROFILE_SUCCESS(HttpStatus.OK, "프로필 조회 성공입니다"),
    GET_NOTICES_WITHOUT_LOGIN(HttpStatus.OK, "공지사항 조회 성공");


    private final HttpStatus httpStatus;
    private final String message;
}
