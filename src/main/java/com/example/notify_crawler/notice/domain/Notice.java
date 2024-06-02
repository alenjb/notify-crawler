package com.example.notify_crawler.notice.domain;

import com.example.notify_crawler.common.domain.BaseTimeEntity;
import com.example.notify_crawler.common.domain.NoticeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.antlr.v4.runtime.misc.NotNull;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Notice extends BaseTimeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @NotNull
    private String noticeTitle;

    private String noticeUrl;

    private Date noticeDate;


    @Enumerated(EnumType.STRING)
    private NoticeType noticeType;

}
