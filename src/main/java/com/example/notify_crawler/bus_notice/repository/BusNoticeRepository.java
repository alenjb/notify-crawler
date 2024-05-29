package com.example.notify_crawler.bus_notice.repository;

import com.example.notify_crawler.bus_notice.domain.BusNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusNoticeRepository extends JpaRepository<BusNotice, Long> {
    BusNotice save(BusNotice notice);
}
