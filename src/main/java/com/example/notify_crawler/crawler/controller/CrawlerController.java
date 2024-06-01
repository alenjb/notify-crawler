package com.example.notify_crawler.crawler.controller;

import com.example.notify_crawler.common.domain.NoticeType;
import com.example.notify_crawler.crawler.service.CrawlerService;
import com.example.notify_crawler.notice.domain.Notice;
import com.example.notify_crawler.notice.repository.NoticeRepository;
//import com.example.notify_crawler.producer.KafkaProducer;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class CrawlerController {

    @Autowired
    private final CrawlerService crawlerService;
    @Autowired
    private final NoticeRepository noticeRepository;
//    @Autowired
//    private final KafkaProducer kafkaProducer;

    public CrawlerController(CrawlerService crawlerService, NoticeRepository noticeRepository) {
        this.crawlerService = crawlerService;
        this.noticeRepository = noticeRepository;
//        this.kafkaProducer = kafkaProducer;
    }


    /**
     * 공지사항의 새 글을 가져와 DB에 저장한다.
     * @throws InterruptedException
     * @throws ParseException
     */
    @Scheduled(cron = "0 */30 * * * *") // 매 30분마다 실행
    @Transactional
    @PostConstruct
    public void getAllNotice() throws InterruptedException, ParseException {
        log.info("==== "+ "새 공지사항 크롤링 시작 시각: "+ String.valueOf(LocalDateTime.now())+"====");
        // Headless 모드로 Chrome 실행
        ChromeOptions options = new ChromeOptions();
        // Headless 모드 활성화
        options.addArguments("--headless");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--ignore-ssl-errors=yes");
        options.addArguments("--ignore-certificate-errors");

        // WebDriver 인스턴스 생성
        WebDriver driver = new ChromeDriver(options);
        NoticeType[] noticeTypes = NoticeType.values();
        // 모든 공지 사항에 대해 반복
        for (NoticeType noticeType : noticeTypes) {
            // 새 글의 개수
            int newNoticeCount = crawlerService.getNewNoticeCount(
                    noticeType,
                    driver,
                    crawlerService.getLastTwoNotices(noticeType));

            // 새 글이 있으면
            if(newNoticeCount > 0){
                // 새 글들을 저장할 리스트
                List<Notice> newNotices = new ArrayList<>();

                // 새 글을 크롤링할 페이지 수
                int newNoticePageCount = newNoticeCount / noticeType.getNoticeSizePerPage();
                // 새 글 수를 페이지 수로 나누었을 때 떨어지는 나머지 값
                int newNoticeNumInLastPage = newNoticeCount % noticeType.getNoticeSizePerPage();

                // 페이지에서 일부만 크롤링해야하는 새 글들을 크롤링 해오기
                List<Notice> newNoticesFromLastPage = crawlerService.getNewNoticesByPageNum(newNoticePageCount + 1, noticeType, driver);
                for(int i=1; i<=newNoticeNumInLastPage; i++){
                    Notice notice = newNoticesFromLastPage.get(noticeType.getNoticeSizePerPage() - i);
                    newNotices.add(notice);
                }
                // 페이지를 통채로 가져올 수 있는 만큼 새 글을 크롤링 해오기
                for(int i=newNoticePageCount; i>=1 ;i--){
                    List<Notice> notices = crawlerService.getNewNoticesByPageNum(i, noticeType, driver);
                    for (Notice notice : notices) {
                        newNotices.add(notice);
                    }
                }
                // 가져온 새 공통 공지사항들 DB에 저장
                crawlerService.saveNewNotices(newNotices, noticeType);
                log.info("==== "+ noticeType + " 새 공지사항 저장 완료 시각: "+ String.valueOf(LocalDateTime.now())+"====");
//                kafkaProducer.sendMessage("공지사항 저장 완료");
            }
        }
    }
}
