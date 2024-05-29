package com.example.notify_crawler.common.exception.model;

import com.example.notify_crawler.common.exception.enums.ErrorCode;

public class UnAuthorizedException extends NotifyException{

    public UnAuthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
