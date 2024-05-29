package com.example.notify_crawler.com_notice.repository;

import com.example.notify_crawler.com_notice.domain.ComNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComNoticeRepository extends JpaRepository<ComNotice, Long> {
    ComNotice save(ComNotice notice);
}
