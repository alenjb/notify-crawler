package com.example.notify_crawler.common.exception.model;

import com.example.notify_crawler.common.exception.enums.ErrorCode;

public class BadRequestException extends NotifyException {
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
