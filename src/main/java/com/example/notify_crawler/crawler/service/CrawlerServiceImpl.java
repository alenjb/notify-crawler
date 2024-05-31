package com.example.notify_crawler.crawler.service;

import com.example.notify_crawler.aai_notice.domain.AaiNotice;
import com.example.notify_crawler.aai_notice.repository.AaiNoticeRepository;
import com.example.notify_crawler.bus_notice.domain.BusNotice;
import com.example.notify_crawler.bus_notice.repository.BusNoticeRepository;
import com.example.notify_crawler.com_notice.domain.ComNotice;
import com.example.notify_crawler.com_notice.repository.ComNoticeRepository;
import com.example.notify_crawler.common.constants.CrawlerConstants;
import com.example.notify_crawler.common.domain.NoticeType;
import com.example.notify_crawler.common.exception.enums.ErrorCode;
import com.example.notify_crawler.common.exception.model.NotFoundException;
import com.example.notify_crawler.cos_notice.domain.CosNotice;
import com.example.notify_crawler.cos_notice.repository.CosNoticeRepository;
import com.example.notify_crawler.crawler.dto.TitlesAndDates;
import com.example.notify_crawler.crawler.repository.CrawlerRepository;
import com.example.notify_crawler.esm_notice.domain.EsmNotice;
import com.example.notify_crawler.esm_notice.repository.EsmNoticeRepository;
import com.example.notify_crawler.notice.domain.Notice;
import com.example.notify_crawler.notice.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.NotNull;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.*;

@Service
@Slf4j
public class CrawlerServiceImpl implements CrawlerService{

    @Autowired
    CrawlerRepository crawlerRepository;
    @Autowired
    ComNoticeRepository comNoticeRepository;
    @Autowired
    BusNoticeRepository busNoticeRepository;
    @Autowired
    AaiNoticeRepository aaiNoticeRepository;
    @Autowired
    CosNoticeRepository cosNoticeRepository;
    @Autowired
    EsmNoticeRepository esmNoticeRepository;
    @Autowired
    NoticeRepository noticeRepository;



    /** ================ 모든 공지사항 관련 기능 (공통, 학과 모두 사용 가능) ================ **/

    /**
     * DB에서 가장 최신의 글 2개의 제목과 날짜를 가져온다.
     * @param noticeType 공지사항의 타입
     * @return 제목과 날짜를 매핑한 객체
     */
    @Override
    public String[][] getLastTwoNotices(NoticeType noticeType) {
        List<Notice> top2 = crawlerRepository.findTop2ByOrderByCreatedAtDesc(noticeType);
        String [][] result = new String[2][2]; // 각 행의 0번째 인덱스에는 제목이 1번쨰 인덱스에는 날짜가 들어있음. 0번쨰 행이 더 최신 글임

        for (int i=0; i<2; i++) {
            String noticeTitle = top2.get(i).getNoticeTitle();
            Date noticeDate = top2.get(i).getNoticeDate();
            result[i][0] = noticeTitle;
            result[i][1] = new Date(noticeDate.getTime()).toString();
        }
        return result;
    }

    /**
     * 새 글의 개수를 반환한다.
     * @param noticeType 공지사항의 타입
     * @param driver 크롬 드라이버
     * @param top2 DB에 저장된 가장 최근 게시물 2개
     * @return
     */
    @Override
    public int getNewNoticeCount(NoticeType noticeType, WebDriver driver, String [][] top2) throws InterruptedException, ParseException {
        // 페이지에서 제목 목록과 날짜 목록을 가져오기
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        TitlesAndDates titlesAndDates = null;
        // 공통 공지사항인 경우 첫 페이지의 글들을 가져오기
        if(noticeType == NoticeType.COM) titlesAndDates = getTitlesAndDatesOfComNoticeFromPageNum(driver, 1);
            // 학과 공지사항인 경우 첫 페이지의 글들을 가져오기
        else titlesAndDates = getTitlesAndDatesOfMajorNoticeFromPageNum(driver, 1, noticeType);

        boolean hasNewNotice = newNoticeCheck(top2, titlesAndDates.titles(), titlesAndDates.dates());
        // 새 글이 없는 경우 0을 반환
        if(!hasNewNotice) return 0;
            // 새 글이 있는 경우 새 글이 몇개 있는지 반환
        else return findNewNoticeOrder(top2, driver, noticeType);
    }

    /**
     * 공지사항의 새 글의 개수를 찾는다.
     * @param top2 DB에 저장된 가장 최근 게시물 2개
     * @param driver 크롬 드라이버
     * @param noticeType 공지사항의 타입
     * @return 새 글의 개수
     * @throws InterruptedException
     * @throws ParseException
     */
    @Override
    public int findNewNoticeOrder(String[][] top2, WebDriver driver, NoticeType noticeType) throws InterruptedException, ParseException {
        String firstTitle = top2[0][0]; // DB의 첫번째(제일 최근) 게시물의 제목
        String firstDate = top2[0][1]; // DB의 첫번째(제일 최근) 게시물의 날짜
        String secondTitle = top2[1][0]; // DB의 두번째 게시물의 제목
        String secondDate = top2[1][1]; // DB의 두번째 게시물의 날짜

        // 1페이지부터 10페이지까지 반복하면서
        for(int i=1; i<=10; i++){
            // 해당 페이지로 이동
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement pageButton;
            if(noticeType == NoticeType.COM){ // 공통 공지사항인 경우
                pageButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.dhx_page:nth-of-type(" + i + ")")));
            }else { // 학과 공지사항인 경우
                pageButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".paging-wrap > li:nth-of-type(" + i + ")")));
            }
            pageButton.click();


            // 클릭이 정상적으로 적용되도록 0.5초 대기
            Thread.sleep(500);

            TitlesAndDates titlesAndDates = null;
            // 페이지에 있는 공통 공지사항들의 제목과 날짜 가져오기
            // 공통 공지사항인 경우
            if(noticeType == NoticeType.COM) titlesAndDates = getTitlesAndDatesOfComNoticeFromPageNum(driver, i);
                // 학과 공지사항인 경우
            else titlesAndDates = getTitlesAndDatesOfMajorNoticeFromPageNum(driver, i, noticeType);



            // Date 형식인 titlesAndDates.dates()와 firstDate, secondDate를 비교하기 위해 날짜 객체를 문자로 바꿔서 리스트 생성
            List<String> StringDates = new ArrayList<>();
            for (Date date : titlesAndDates.dates()) {
                StringDates.add(date.toString());
            }

            // DB의 첫번째 게시물의 인덱스를 찾으면 새글의 개수를 반환
            if(titlesAndDates.titles().contains(firstTitle)
                    && titlesAndDates.titles().indexOf(firstTitle) == StringDates.indexOf(firstDate))
                return (i-1) * noticeType.getNoticeSizePerPage() + titlesAndDates.titles().indexOf(firstTitle);

                // DB의 두번째 게시물의 인덱스를 찾으면 첫번째 게시물이 수정이나 삭제되었다고 여기고 두번째 게시글 이후를 새글로 간주 후 개수를 반환
            else if(titlesAndDates.titles().contains(secondTitle)
                    && titlesAndDates.titles().indexOf(secondTitle) == StringDates.indexOf(secondDate))
                return (i-1) * noticeType.getNoticeSizePerPage() + titlesAndDates.titles().indexOf(secondTitle);
        }
        // 10페이지까지 반복해도 새 게시물이 없는 경우 예외 발생
        throw new NotFoundException(ErrorCode.NOT_FOUND_RESOURCE_EXCEPTION);
    }

    /**
     * 새 공지사항의 유무를 체크한다.
     * @param top2 DB에 저장된 가장 최근 게시물 2개
     * @param titles 페이지에서 가져온 제목들
     * @param dates 페이지에서 가져온 날짜들
     * @return 새 글의 유무
     */
    @Override
    public boolean newNoticeCheck(String[][] top2, List<String> titles, List<Date> dates) {
        String firstTitle = top2[0][0]; // 가장 최신 글의 제목
        String firstDate = top2[0][1]; // 가장 최신 글의 날짜
        String secondTitle = top2[1][0]; // 두번째 최신 글의 제목
        String secondDate = top2[1][1]; // 두번째 최신 글의 제목
        boolean hasFirst = false; // 페이지에 가장 최신글의 존재 유무
        boolean hasSecond = false; // 페이지에 두번째 최신글의 존재 유무

        // 가장 최신의 글 2개가 DB의 가장 최신의 글 두개와 일치하는지 확인
        for(int i=0; i<2; i++){
            if(titles.get(i).equals(firstTitle) && dates.get(i).toString().equals(firstDate)) hasFirst = true;
            else if(titles.get(i).equals(secondTitle) && dates.get(i).toString().equals(secondDate)) hasSecond = true;
        }
        return !(hasFirst && hasSecond);
    }


    /** ================ 공통 공지사항 관련 기능 ================ **/

    /**
     * 공통 공지사항에 접근하는 경우 학교 사이트에 로그인 후 게시판 버튼을 클릭하여 게시판 페이지에 진입한다.
     * @param driver 크롬 드라이버
     * @param username 로그인 ID
     * @param password 로그인 PW
     */
    @Override
    public WebDriver loginAndGoToComNoticePage(WebDriver driver, String username, String password) {

        // 웹 페이지 열기
        try {
            // 웹 페이지 열기
            driver.get(CrawlerConstants.LOGIN_PAGE);
        } catch (NoSuchSessionException e) {

            // 세션 다시 시작
            driver.quit();
            // Headless 모드로 Chrome 실행
            ChromeOptions options = getChromeOptions();
            // WebDriver 인스턴스 생성
            driver = new ChromeDriver(options); // 새로운 WebDriver 인스턴스 생성
            // 다시 시도
            driver.get(CrawlerConstants.LOGIN_PAGE);
        }
        // 아이디와 비밀번호 입력
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // 최대 10초간 대기
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userid")));
        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userpwd")));
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);

        // 로그인 버튼 클릭
        WebElement loginButton = driver.findElement(By.id("loginBtn"));
        loginButton.click();

        // 게시판 버튼 클릭
        WebElement boardButton = wait.until(ExpectedConditions.presenceOfElementLocated
                (By.cssSelector("#mypage > form:nth-child(16) > div > div.pageHeader > div.mainMenu > div > ul > li.board > a > span.ico > img")));
        boardButton.click();

        return driver;
    }

    /**
     * 공지사항 페이지에서 공통 공지사항의 제목과 날짜를 가져온다.
     * @param driver 크롬 드라이버
     * @return 공지사항 제목과 날짜 리스트
     */
    @NotNull
    @Override
    public TitlesAndDates getTitlesAndDatesOfComNoticeFromPageNum(WebDriver driver, int pageNum) throws ParseException {

        driver.get(CrawlerConstants.COM_NOTICE_BOARD_PAGE);

        // 두번째 iframe 요소 가져오기
        WebElement secondIframe = driver.findElement(By.xpath("(//iframe)[2]"));
        // 두 번째 iframe으로 전환
        driver.switchTo().frame(secondIframe);

        // 해당 페이지로 이동
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement pageButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.dhx_page:nth-of-type(" + pageNum + ")")));
        pageButton.click();

        // 클래스 이름이 .ev_dhx_terrace 인 모든 요소 가져오기
        List<WebElement> dynamicElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".ev_dhx_terrace")));

        // 타이틀과 날짜를 저장할 리스트 생성
        List<String> titles = new ArrayList<>();
        List<Date> dates = new ArrayList<>();

        // 가져온 요소들에서 타이틀과 날짜 추출하여 리스트에 저장
        for (WebElement element : dynamicElements) {
            // 타이틀 추출
            WebElement titleElement = element.findElement(By.cssSelector("span"));
            titles.add(titleElement.getText());

            // 날짜 추출
            WebElement dateElement = element.findElement(By.cssSelector("td:nth-child(6)"));
            Date date = parseComNoticeDateAndFormatting(dateElement.getText());
            dates.add(date);
        }

        // TitlesAndDates 객체에 저장해 반환
        return new TitlesAndDates(titles, dates);
    }

    /**
     * 해당 페이지 번호에서 공통 공지사항들을 가져온다.
     * @param username 로그인 ID
     * @param password 로그인 PW
     * @param pageNum 가져올 페이지 번호
     * @param oldDriver 크롬 드라이버
     * @return 해당 페이지 번호의 공지사항들 리스트
     * @throws InterruptedException
     * @throws ParseException
     */
    @Override
    public List<Notice> getNewComNoticesByPageNum(String username, String password, int pageNum, WebDriver oldDriver) throws InterruptedException, ParseException {
        oldDriver.quit();
        ChromeOptions options = getChromeOptions();

        WebDriver driver = new ChromeDriver(options);
        // 공지사항들을 저장할 리스트
        List<Notice> notices = new ArrayList<>();
        //로그인 하기
        if(!isLoggedIn(driver)) {
            driver = loginAndGoToComNoticePage(driver, username, password);
        }

        // 공지 페이지로 이동
        driver.get(CrawlerConstants.COM_NOTICE_BOARD_PAGE);

        // 두번째 iframe 요소 가져오기
        WebElement secondIframe = driver.findElement(By.xpath("(//iframe)[2]"));
        // 두 번째 iframe으로 전환
        driver.switchTo().frame(secondIframe);

        // 해당 페이지로 이동
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // 최대 10초간 대기
        WebElement nextButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.dhx_page:nth-of-type(" + pageNum + ")")));
        nextButton.click();

        //페이지의 이동을 위해 0.5초 대기
        Thread.sleep(500);

        // 클래스 이름이 .ev_dhx_terrace 인 모든 요소 가져오기
        List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".ev_dhx_terrace")));
        String currentHandle = driver.getWindowHandle();
        // 각 공지사항을 클릭
        for (WebElement element : elements) {
            element.click();
        }

        // 클릭이 제대로 진행될 때까지 1초 대기
        Thread.sleep(1000);
        // 새로 열린 창의 핸들을 가져오기
        Set<String> windowHandles = driver.getWindowHandles();
        // 모든 창에서 공지사항 정보 가져오기
        for (String windowHandle : windowHandles) {
            if(!windowHandle.equals(currentHandle)) {
                String newHandle = windowHandle;
                driver.switchTo().window(newHandle);

                // 새로 열린 창이 완전히 로드될 때까지 대기
                wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

                // 페이지에서 URL을 찾아 문자열로 저장
                String noticeUrl = driver.getCurrentUrl();

                // 페이지에서 공지 제목을 찾아 문자열로 변환
                WebElement title = driver.findElement(By.cssSelector(".searom_tit"));
                String noticeTitle = title.getText();

                // 페이지에서 공지 날짜를 찾아 날짜 객체로 변환
                WebElement date = driver.findElement(By.id("reg_info"));
                String dateText = date.getText();
                Date noticeDate = parseComNoticeDateAndFormatting(dateText);

                // noticeDate를 가져올 때까지 기다리기
                Thread.sleep(100);

                // 공지 객체로 만들기
                Notice notice = Notice.builder()
                        .noticeTitle(noticeTitle)
                        .noticeDate(noticeDate)
                        .noticeUrl(noticeUrl)
                        .noticeType(NoticeType.COM)
                        .build();
                notices.add(notice);

                // 창 닫기
                driver.close();
            }
        }
        // 현재 창으로 전환
        driver.switchTo().window(currentHandle);
        // 창 닫기
        driver.close();

        // noticeDate 오래된 순으로 정렬(나중에 DB에 넣을 때 오래된 것을 먼저 삽입해야하므로)
        Collections.sort(notices, new DateComparator());
        return notices;
    }

    /**
     * 날짜를 오래된 순으로 정렬하기 위해 비교한다.
     */
    public class DateComparator implements Comparator<Notice> {
        @Override
        public int compare(Notice notice1, Notice notice2) {
            // Date 필드를 기준으로 내림차순 정렬
            return notice1.getNoticeDate().compareTo(notice2.getNoticeDate());
        }
    }

    /**
     * 페이지에서 가져온 공통 공지사항들의 날짜 텍스트를 Date 객체로 변환한다.
     * @param input 페이지에서 가져온 공통 공지사항의 날짜 텍스트
     * @return Date 객체로 변환한 공통 공지사항의 날짜
     * @throws ParseException
     */
    @Override
    public Date parseComNoticeDateAndFormatting(String input) throws ParseException {

        // 공백으로 나누기
        String[] parts = input.split(" ");
        int length = parts.length;

        // 문자열에서 각 요소 파싱하기
        String dateString = parts[length-3].trim().replace("(",""); // 년/월/일 추출
        String dayString = parts[length-2].replace("(","").replace(")",""); // 요일 추출
        String timeString = parts[length-1].replace(")",""); // 시간 추출
        String totalString = dateString+" "+dayString+" "+timeString;

        // 한국어로 되어있는 날짜를 변환하는 포맷
        SimpleDateFormat koreanInputFormat = new SimpleDateFormat("yyyy/MM/dd E HH:mm", Locale.KOREAN);
        // 영어로 되어있는 날짜를 변환하는 포맷
        SimpleDateFormat englishInputFormat = new SimpleDateFormat("yyyy/MM/dd E HH:mm", Locale.ENGLISH);

        // 날짜와 요일, 시간 정보를 Date 객체로 변환
        try {
            // 한국어로 파싱 시도
            return koreanInputFormat.parse(totalString);
        } catch (ParseException e) {
            try {
                // 한국어로 파싱이 실패하면 영어로 파싱 시도
                return englishInputFormat.parse(totalString);
            } catch (ParseException ex) {
                // 둘 다 파싱이 실패하면 예외 처리
                throw new ParseException(ex.getMessage(), ex.getErrorOffset());
            }
        }
    }

    /**
     * 로그인 메서드를 통해 이미 로그인 되었는지 확인한다.
     * @param driver 크롬 드라이버
     * @return 로그인 유무
     */
    @Override
    public boolean isLoggedIn(WebDriver driver){
        // 웹 페이지 열기
        driver.get(CrawlerConstants.LOGIN_PAGE);

        // 아이디 입력 요소 존재 확인
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3)); // 최대 3초간 대기
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userid")));
            driver.close();
            // 아이디 입력 요소가 존재하면 로그인되지 않은 상태로 간주하여 false 반환
            return false;
        } catch (NoSuchElementException e) {
            driver.close();
            // 아이디 입력 요소가 없으면 로그인된 상태로 간주하여 true 반환
            return true;
        }
    }

    /**
     * 새 공통 공지사항들을 DB에 저장한다.
     * @param newNotices 새 공통 공지사항들 리스트
     */
    @Transactional
    public void saveNewComNotices(List<Notice> newNotices) {
        for (Notice notice : newNotices) {
            // 공지사항 객체화
            Notice build = Notice.builder()
                    .noticeTitle(notice.getNoticeTitle())
                    .noticeDate(notice.getNoticeDate())
                    .noticeUrl(notice.getNoticeUrl())
                    .noticeType(notice.getNoticeType())
                    .build();
            // 공지사항 테이블에 저장
            noticeRepository.save(build);

            //공통 공지사항 객체화
            ComNotice comNotice = new ComNotice();
            // 공지사항 테이블의 아이디를 가져오기
            comNotice.setNoticeId(build.getNoticeId());
            //공통 공지사항 테이블에 저장
            comNoticeRepository.save(comNotice);
        }
    }

    /** ================ 학과 공지사항 관련 기능 ================ **/

    /**
     * 학과 공지사항 페이지에 접속해서 제목과 날짜를 가져온다.
     * @param driver 크롬 드라이버
     * @param pageNum 가져올 페이지 번호
     * @param noticeType 학과
     * @return 공지사항 제목과 날짜 리스트
     * @throws ParseException
     */
    @Override
    public TitlesAndDates getTitlesAndDatesOfMajorNoticeFromPageNum(WebDriver driver, int pageNum, NoticeType noticeType) throws ParseException {

        // 해당 페이지로 이동
        driver.get(noticeType.getBoardUrl());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement pageButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".paging-wrap > li:nth-of-type(" + pageNum + ")")));
        pageButton.click();

        List<WebElement> titleAndUrls = driver.findElements(By.cssSelector(
                "dt.board-list-content-title:not(.board-list-content-top) a"));

        List<WebElement> pageDates = driver.findElements(By.cssSelector("dt.board-list-content-title:not(.board-list-content-top) +dd li:nth-of-type(3)"));        // 요소의 자식인 li 태그들 가져오기

        // 타이틀과 날짜를 저장할 리스트 생성
        List<String> titles = new ArrayList<>();
        List<Date> dates = new ArrayList<>();
        for(int i=0; i<noticeType.getNoticeSizePerPage(); i++) {
            // 페이지에서 제목을 찾아 문자열로 저장
            String noticeTitle = titleAndUrls.get(i).getText();
            titles.add(noticeTitle);

            // 페이지에서 공지 날짜를 찾아 날짜 객체로 변환
            String dateText = pageDates.get(i).getText();
            Date noticeDate = parseMajorNoticeDateAndFormatting(dateText);
            dates.add(noticeDate);
        }

        // TitlesAndDates 객체에 저장해 반환
        return new TitlesAndDates(titles, dates);
    }

    /**
     * 해당 페이지 번호에서 학과 공지사항들을 가져온다.
     * @param pageNum 가져올 페이지 번호
     * @param noticeType 공지사항 타입
     * @param driver 크롬 드라이버
     * @return 해당 페이지 번호의 공지사항들 리스트
     * @throws InterruptedException
     * @throws ParseException
     */
    @Override
    public List<Notice> getNewMajorNoticesByPageNum(int pageNum, NoticeType noticeType, WebDriver driver) throws InterruptedException, ParseException{

        // 공지사항들을 저장할 리스트
        List<Notice> notices = new ArrayList<>();

        // 공지 페이지로 이동
        driver.get(noticeType.getBoardUrl());

        // 해당 페이지로 이동
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement pageButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".paging-wrap > li:nth-of-type(" + pageNum + ")")));
        pageButton.click();

        //페이지의 이동을 위해 0.5초 대기
        Thread.sleep(500);


        List<WebElement> titleAndUrls = driver.findElements(By.cssSelector(
                "dt.board-list-content-title:not(.board-list-content-top) a"));

        List<WebElement> dates = driver.findElements(By.cssSelector("dt.board-list-content-title:not(.board-list-content-top) +dd li:nth-of-type(3)"));        // 요소의 자식인 li 태그들 가져오기

        for(int i=0; i<noticeType.getNoticeSizePerPage(); i++){
            // 페이지에서 URL을 찾아 문자열로 저장
            String noticeTitle = titleAndUrls.get(i).getText();
            String noticeUrl = titleAndUrls.get(i).getAttribute("href");

            // 페이지에서 공지 날짜를 찾아 날짜 객체로 변환
            String dateText = dates.get(i).getText();
            Date noticeDate = parseMajorNoticeDateAndFormatting(dateText);

            // 공지 객체로 만들기
            Notice notice = Notice.builder()
                    .noticeTitle(noticeTitle)
                    .noticeDate(noticeDate)
                    .noticeUrl(noticeUrl)
                    .noticeType(noticeType)
                    .build();
            notices.add(notice);
        }

        // noticeDate 오래된 순으로 정렬(나중에 DB에 넣을 때 오래된 것을 먼저 삽입해야하므로)
        Collections.reverse(notices);
        return notices;
    }


    /**
     * 페이지에서 가져온 학과 공지사항들의 날짜 텍스트를 Date 객체로 변환한다.
     * @param dateString 페이지에서 가져온 학과 공지사항의 날짜 텍스트
     * @return Date 객체로 변환한 학과 공지사항의 날짜
     * @throws ParseException
     */
    @Override
    public Date parseMajorNoticeDateAndFormatting(String dateString) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = format.parse(dateString);
            return date;
        } catch (ParseException e) {
            // 둘 다 파싱이 실패하면 예외 처리
            throw new ParseException(e.getMessage(), e.getErrorOffset());
        }
    }

    /**
     * 새 학과 공지사항들을 DB에 저장한다.
     * @param newNotices 새 학과 공지사항들
     * @param noticeType 공지사항 타입
     */
    @Override
    @Transactional
    public void saveNewMajorNotices(List<Notice> newNotices, NoticeType noticeType) {
        for (Notice notice : newNotices) {
            // 공지사항 객체화
            Notice build = Notice.builder()
                    .noticeTitle(notice.getNoticeTitle())
                    .noticeDate(notice.getNoticeDate())
                    .noticeUrl(notice.getNoticeUrl())
                    .noticeType(notice.getNoticeType())
                    .build();
            // 공지사항 테이블에 저장
            noticeRepository.save(build);
            switch (noticeType){
                case BUS:
                    // 경영학과 공지사항 객체화
                   BusNotice busNotice = new BusNotice();
                    // 공지사항 테이블의 아이디를 가져오기
                    busNotice.setNoticeId(build.getNoticeId());
                    // 경영학과 공지사항 테이블에 저장
                    busNoticeRepository.save((BusNotice) busNotice);
                    break;
                case AAI:
                    // 인공지능융합학과 공지사항 객체화
                    AaiNotice aaiNotice = new AaiNotice();
                    // 공지사항 테이블의 아이디를 가져오기
                    aaiNotice.setNoticeId(build.getNoticeId());
                    // 인공지능융합학과 공지사항 테이블에 저장
                    aaiNoticeRepository.save((AaiNotice) aaiNotice);
                    break;
                case COS:
                    // 유학동양학과 공지사항 객체화
                    CosNotice cosNotice = new CosNotice();
                    // 공지사항 테이블의 아이디를 가져오기
                    cosNotice.setNoticeId(build.getNoticeId());
                    // 유학동양학과 공지사항 테이블에 저장
                    cosNoticeRepository.save((CosNotice) cosNotice);
                    break;
                case ESM:
                    // 시스템경영공학과 공지사항 객체화
                    EsmNotice esmNotice = new EsmNotice();
                    // 공지사항 테이블의 아이디를 가져오기
                    esmNotice.setNoticeId(build.getNoticeId());
                    // 시스템경영공학과 공지사항 테이블에 저장
                    esmNoticeRepository.save((EsmNotice) esmNotice);
                    break;
            }
        }
    }

    public static ChromeOptions getChromeOptions() {
        // Headless 모드로 Chrome 실행
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-debugging-port=9222");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-features=VizDisplayCompositor");
        return options;
    }
}