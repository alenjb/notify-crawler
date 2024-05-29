package com.example.notify_crawler.common.exception.model;

import com.example.notify_crawler.common.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundUserException extends NotifyException {

    public NotFoundUserException(ErrorCode errorCode) {
        super(errorCode);

    }
}
