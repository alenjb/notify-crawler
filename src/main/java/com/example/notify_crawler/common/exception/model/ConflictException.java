package com.example.notify_crawler.common.exception.model;

import com.example.notify_crawler.common.exception.enums.ErrorCode;

public class ConflictException extends NotifyException {
    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }
}
