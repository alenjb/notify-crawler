package com.example.notify_crawler.aai_notice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AaiNotice {
    @Id
    private Long noticeId;
}
