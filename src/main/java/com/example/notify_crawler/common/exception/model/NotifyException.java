package com.example.notify_crawler.common.exception.model;

import com.example.notify_crawler.common.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class NotifyException extends RuntimeException{
    private final ErrorCode errorCode;

    public NotifyException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
