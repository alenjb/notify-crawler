package com.example.notify_crawler.crawler.repository;

import com.example.notify_crawler.common.domain.NoticeType;
import com.example.notify_crawler.notice.domain.Notice;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrawlerRepository extends JpaRepository<Notice, Long>{
    /**
     * 해당하는 타입의 공지사항 테이블에서 가장 최근 2개의 게시물을 가져온다.
     * @param noticeType 공지 타입
     * @return 가장 최근 2개의 게시물 리스트
     */
    @Query("SELECT n FROM Notice n WHERE n.noticeType = :noticeType ORDER BY n.noticeId DESC LIMIT 2")
    List<Notice> findTop2ByOrderByCreatedAtDesc(@Param("noticeType") NoticeType noticeType);

}
