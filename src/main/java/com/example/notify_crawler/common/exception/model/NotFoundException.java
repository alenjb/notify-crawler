package com.example.notify_crawler.common.exception.model;

import com.example.notify_crawler.common.exception.enums.ErrorCode;

public class NotFoundException extends NotifyException{
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
