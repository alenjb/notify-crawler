package com.example.notify_crawler.com_notice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ComNotice{
    @Id
    private Long noticeId;

}
