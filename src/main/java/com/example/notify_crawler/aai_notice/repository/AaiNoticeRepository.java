package com.example.notify_crawler.aai_notice.repository;

import com.example.notify_crawler.aai_notice.domain.AaiNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AaiNoticeRepository extends JpaRepository<AaiNotice, Long> {
    AaiNotice save(AaiNotice notice);

}
