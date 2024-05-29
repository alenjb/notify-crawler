package com.example.notify_crawler.crawler.service;

import com.example.notify_crawler.common.domain.NoticeType;
import com.example.notify_crawler.crawler.dto.TitlesAndDates;
import com.example.notify_crawler.notice.domain.Notice;
import jakarta.transaction.Transactional;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public interface CrawlerService {

    /** ================ 모든 공지사항 관련 기능 (공통, 학과 모두 사용 가능) ================ **/

    /**
     * DB에서 가장 최신의 글 2개의 제목과 날짜를 가져온다.
     * @param noticeType 공지사항의 타입
     * @return 제목과 날짜를 매핑한 객체
     */
    String[][] getLastTwoNotices(NoticeType noticeType);

    /**
     * 새 글의 개수를 반환한다.
     * @param noticeType 공지사항의 타입
     * @param driver 크롬 드라이버
     * @param top2 DB에 저장된 가장 최근 게시물 2개
     * @return
     */
    int getNewNoticeCount(NoticeType noticeType, WebDriver driver, String[][] top2) throws InterruptedException, ParseException;

    /**
     * 공지사항의 새 글의 개수를 찾는다.
     * @param top2 DB에 저장된 가장 최근 게시물 2개
     * @param driver 크롬 드라이버
     * @param noticeType 공지사항의 타입
     * @return 새 글의 개수
     * @throws InterruptedException
     * @throws ParseException
     */
    int findNewNoticeOrder(String[][] top2, WebDriver driver, NoticeType noticeType) throws InterruptedException, ParseException;

    /**
     * 새 공지사항의 유무를 체크한다.
     * @param top2 DB에 저장된 가장 최근 게시물 2개
     * @param titles 페이지에서 가져온 제목들
     * @param dates 페이지에서 가져온 날짜들
     * @return 새 글의 유무
     */
    boolean newNoticeCheck(String[][] top2, List<String> titles, List<Date> dates);


    /** ================ 공통 공지사항 관련 기능 ================ **/

    /**
     * 공통 공지사항에 접근하는 경우 학교 사이트에 로그인 후 게시판 버튼을 클릭하여 게시판 페이지에 진입한다.
     * @param driver 크롬 드라이버
     * @param username 로그인 ID
     * @param password 로그인 PW
     */

    WebDriver loginAndGoToComNoticePage(WebDriver driver, String username, String password);

    /**
     * 공지사항 페이지에서 공통 공지사항의 제목과 날짜를 가져온다.
     * @param driver 크롬 드라이버
     * @return 공지사항 제목과 날짜 리스트
     */
    TitlesAndDates getTitlesAndDatesOfComNoticeFromPageNum(WebDriver driver, int pageNum) throws ParseException;

    /**
     * 해당 페이지 번호에서 공지사항들을 가져온다.
     * @param username 로그인 ID
     * @param password 로그인 PW
     * @param pageNum 가져올 페이지 번호
     * @param driver 크롬 드라이버
     * @return 해당 페이지 번호의 공지사항들 리스트
     * @throws InterruptedException
     * @throws ParseException
     */
    List<Notice> getNewComNoticesByPageNum(String username, String password, int pageNum, WebDriver driver) throws InterruptedException, ParseException;

    /**
     * 페이지에서 가져온 공통 공지사항들의 날짜 텍스트를 Date 객체로 변환한다.
     * @param input 페이지에서 가져온 공통 공지사항의 날짜 텍스트
     * @return Date 객체로 변환한 공통 공지사항의 날짜
     * @throws ParseException
     */
    Date parseComNoticeDateAndFormatting(String input) throws ParseException;

    /**
     * 로그인 메서드를 통해 이미 로그인 되었는지 확인한다.
     * @param driver 크롬 드라이버
     * @return 로그인 유무
     */
    boolean isLoggedIn(WebDriver driver);

    /**
     * 새 공통 공지사항들을 DB에 저장한다.
     * @param newNotices 새 공통 공지사항들 리스트
     */
    @Transactional
    void saveNewComNotices(List<Notice> newNotices);

    /** ================ 학과 공지사항 관련 기능 ================ **/

    /**
     * 학과 공지사항 페이지에 접속해서 제목과 날짜를 가져온다.
     * @param driver 크롬 드라이버
     * @param pageNum 가져올 페이지 번호
     * @param noticeType 학과
     * @return 공지사항 제목과 날짜 리스트
     * @throws ParseException
     */
    TitlesAndDates getTitlesAndDatesOfMajorNoticeFromPageNum(WebDriver driver, int pageNum, NoticeType noticeType) throws ParseException;

    /**
     * 해당 페이지 번호에서 학과 공지사항들을 가져온다.
     * @param pageNum 가져올 페이지 번호
     * @param noticeType 공지사항 타입
     * @param driver 크롬 드라이버
     * @return 해당 페이지 번호의 공지사항들 리스트
     * @throws InterruptedException
     * @throws ParseException
     */
    List<Notice> getNewMajorNoticesByPageNum(int pageNum, NoticeType noticeType, WebDriver driver) throws InterruptedException, ParseException;

    /**
     * 페이지에서 가져온 학과 공지사항들의 날짜 텍스트를 Date 객체로 변환한다.
     * @param dateString 페이지에서 가져온 학과 공지사항의 날짜 텍스트
     * @return Date 객체로 변환한 학과 공지사항의 날짜
     * @throws ParseException
     */
    Date parseMajorNoticeDateAndFormatting(String dateString) throws ParseException;

    /**
     * 새 학과 공지사항들을 DB에 저장한다.
     * @param newNotices 새 학과 공지사항들
     * @param noticeType 공지사항 타입
     */
    @Transactional
    void saveNewMajorNotices(List<Notice> newNotices, NoticeType noticeType);

}
