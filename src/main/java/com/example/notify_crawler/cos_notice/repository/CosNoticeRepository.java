package com.example.notify_crawler.cos_notice.repository;

import com.example.notify_crawler.cos_notice.domain.CosNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CosNoticeRepository extends JpaRepository<CosNotice, Long> {
    CosNotice save(CosNotice notice);
}
