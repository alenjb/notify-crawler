package com.example.notify_crawler.common.constants;

public final class CrawlerConstants {
    /** ================ 공통 공지사항 관련 상수 ================ **/
    // 한 페이지에 크롤링 해오는 공통 공지사항 개수
    public static final int CRAWLING_COM_NOTICE_SIZE_PER_PAGE = 15;
    // 로그인 페이지 url
    public static final String LOGIN_PAGE = "https://eportal.skku.edu";
    // 공통 공지사항 게시판 페이지 url
    public static final String COM_NOTICE_BOARD_PAGE = "https://app.skku.edu/portal.nsf/boardPortal?readForm";

    /** ================ 학과 공지사항 관련 상수 ================ **/
    /* 1. 경영학과 */
    // 한 페이지에 크롤링 해오는 경영학과 공지사항 개수
    public static final int CRAWLING_BUS_NOTICE_SIZE_PER_PAGE = 10;
    // 경영학과 공지사항 게시판 페이지 url
    public static final String BUS_NOTICE_BOARD_PAGE = "https://biz.skku.edu/bizskk/notice.do";

    /* 2. 유학동양학과 */
    // 한 페이지에 크롤링 해오는 유학동양학과 공지사항 개수
    public static final int CRAWLING_COS_NOTICE_SIZE_PER_PAGE = 10;
    // 유학동양학과 공지사항 게시판 페이지 url
    public static final String COS_NOTICE_BOARD_PAGE = "https://scos.skku.edu/scos/community/under_notice.do";

    /* 3. 인공지능융합학과 */
    // 한 페이지에 크롤링 해오는 인공지능융합학과 공지사항 개수
    public static final int CRAWLING_AAI_NOTICE_SIZE_PER_PAGE = 10;
    // 인공지능융합학과 공지사항 게시판 페이지 url
    public static final String AAI_NOTICE_BOARD_PAGE = "https://xai.skku.edu/skkuaai/notice.do";

    /* 4. 시스템경영공학과 */
    // 한 페이지에 크롤링 해오는 시스템경영공학과 공지사항 개수
    public static final int CRAWLING_ESM_NOTICE_SIZE_PER_PAGE = 10;
    // 시스템경영공학과 공지사항 게시판 페이지 url
    public static final String ESM_NOTICE_BOARD_PAGE = "https://sme.skku.edu/iesys/notice.do";


}
