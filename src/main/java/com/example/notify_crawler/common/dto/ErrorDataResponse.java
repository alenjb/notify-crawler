package com.example.notify_crawler.common.dto;

import com.example.notify_crawler.common.exception.enums.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorDataResponse<T> extends Response{
    private final int code;
    private final String message;
    private final T data;

    public static <T> ErrorDataResponse<T> error(ErrorCode errorCode, T data) {
        return new ErrorDataResponse<>(errorCode.getHttpStatus().value(), errorCode.getMessage(), data);
    }
}